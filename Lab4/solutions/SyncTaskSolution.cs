using System.Data;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Text;
using ConsoleApp1.domain;

namespace ConsoleApp1.solutions;

public class SyncTaskSolution
{
    private static List<string> _hosts;
    public static string answer = string.Empty;

    /************** Run ******************/

    public static void Run(List<string> hostnames)
    {
        _hosts = hostnames;
        var tasks = new List<Task>();

        for (var i = 0; i < hostnames.Count; i++)
        {
            tasks.Add(Task.Factory.StartNew(Start, i));
        }

        Task.WaitAll(tasks.ToArray());
    }

    private static void Start(object idObject)
    {
        var id = (int)idObject;
        StartClient(_hosts[id], id);
    }


    /************** Start Client ******************/

    private static void StartClient(string host, int id)
    {
        var ipHost = Dns.GetHostEntry(host.Split('/')[0]);
        var ipAddress = ipHost.AddressList[0];
        var remoteEndPoint = new IPEndPoint(ipAddress, 80);

        var clientSocket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        var requestSocket = new MySocket
        {
            customSocket = clientSocket,
            Hostname = host.Split('/')[0],
            Endpoint = host.Contains("/") ? host[host.IndexOf("/", StringComparison.Ordinal)..] : "/",
            RemoteIpEndPoint = remoteEndPoint,
            Id = id
        };

        // connect
        Connect(requestSocket).Wait();

        // request data
        Send(requestSocket, DataParser.GetRequestString(requestSocket.Hostname, requestSocket.Endpoint)).Wait();

        // receive response
        Receive(requestSocket).Wait();

        Console.WriteLine("Connection {0} > Content length is:{1}", requestSocket.Id,
            DataParser.GetLengthContent(requestSocket.Response.ToString()));
        clientSocket.Shutdown(SocketShutdown.Both);
        clientSocket.Close();
    }

    /************** Connect ******************/

    private static Task Connect(MySocket state)
    {
        state.customSocket.BeginConnect(state.RemoteIpEndPoint, ConnectCallback, state);
        // block the current thread until the current WaitHandle receives a signal
        return Task.FromResult(state.ConnectDone.WaitOne());
    }

    private static void ConnectCallback(IAsyncResult ar)
    {
        var resultSocket = (MySocket)ar.AsyncState;

        if (resultSocket == null)
        {
            return;
        }

        var clientSocket = resultSocket.customSocket;
        var clientId = resultSocket.Id;
        var hostName = resultSocket.Hostname;

        clientSocket.EndConnect(ar);

        Console.WriteLine("Connection {0} > Socket connected to {1} ({2})", clientId, hostName,
            clientSocket.RemoteEndPoint);

        // Set the state of the event to signaled, allowing one or more waiting threads to proceed.
        resultSocket.ConnectDone.Set();
    }

    /************** Send ******************/

    private static Task Send(MySocket state, string data)
    {
        var byteData = Encoding.ASCII.GetBytes(data);

        // send the data
        state.customSocket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, state);
        return Task.FromResult(state.SendDone.WaitOne());
    }

    private static void SendCallback(IAsyncResult ar)
    {
        var resultSocket = (MySocket)ar.AsyncState;

        if (resultSocket == null)
        {
            return;
        }

        var clientSocket = resultSocket.customSocket;
        var clientId = resultSocket.Id;

        // end a pending async send
        var bytesSent = clientSocket.EndSend(ar);
        Console.WriteLine("Connection {0} > Sent {1} bytes to server.", clientId, bytesSent);

        resultSocket.SendDone.Set();
    }

    /************** Receive ******************/

    private static Task Receive(MySocket state)
    {
        state.customSocket.BeginReceive(state.Buffer, 0, MySocket.Size, 0, ReceiveCallback, state);
        return Task.FromResult(state.ReceiveDone.WaitOne());
    }

    private static void ReceiveCallback(IAsyncResult ar)
    {
        var resultSocket = (MySocket)ar.AsyncState;

        if (resultSocket == null)
        {
            return;
        }

        var clientSocket = resultSocket.customSocket;

        try
        {
            var bytesRead = clientSocket.EndReceive(ar);

            if (bytesRead > 0)
            {
                resultSocket.Response.Append(Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead));
                clientSocket.BeginReceive(resultSocket.Buffer, 0, MySocket.Size, 0, ReceiveCallback, resultSocket);
                Console.WriteLine("Content is - {0}", resultSocket.Response);
            }
            else
            {
                if (resultSocket.Response.Length > 1)
                {
                    answer = resultSocket.Response.ToString();
                }

                resultSocket.ReceiveDone.Set();
            }
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
}