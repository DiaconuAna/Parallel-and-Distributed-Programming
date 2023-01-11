using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using ConsoleApp1.domain;

namespace ConsoleApp1.solutions;

public class CallbackSolution
{
    public static string answer = string.Empty;
    public static void Run(List<string> hostnames)
    {
        for (var i = 0; i < hostnames.Count; i++)
        {
            StartClient(hostnames[i], i);
            Thread.Sleep(1000);
        }
    }
    private static void StartClient(string host, int id)
    {
        // get the ip
        var ipHost = Dns.GetHostEntry(host.Split('/')[0]);
        var ipAddress = ipHost.AddressList[0];
        var remoteEndPoint = new IPEndPoint(ipAddress, 80);

        // create tcp socket
        var clientSocket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        var requestSocket = new MySocket{
            customSocket = clientSocket,
            Hostname = host.Split('/')[0],
            Endpoint = host.Contains("/") ? host[host.IndexOf("/", StringComparison.Ordinal)..] : "/",
            RemoteIpEndPoint = remoteEndPoint,
            Id = id
        };
        
        // connect to the remote endpoint
        requestSocket.customSocket.BeginConnect(requestSocket.RemoteIpEndPoint, OnConnect, requestSocket);
    }

    private static void OnConnect(IAsyncResult ar)
    {
        var resultSocket = (MySocket)ar.AsyncState;

        if (resultSocket == null)
        {
            return;
        }

        var clientSocket = resultSocket.customSocket;
        var clientId = resultSocket.Id;
        
        clientSocket.EndConnect(ar);
        Console.WriteLine(" Connection {0} - Socket Connected", clientId);

        var byteData =
            Encoding.ASCII.GetBytes(DataParser.GetRequestString(resultSocket.Hostname, resultSocket.Endpoint));
        
        // connect to the remote endpoint
        resultSocket.customSocket.BeginSend(byteData, 0, byteData.Length, 0, OnSend, resultSocket);
    }

    private static void OnSend(IAsyncResult ar)
    {
        var resultSocket = (MySocket)ar.AsyncState;

        if (resultSocket == null)
        {
            return;
        }
        
        var clientSocket = resultSocket.customSocket;
        var clientId = resultSocket.Id;

        // send data to server
        var bytesSent = clientSocket.EndSend(ar);
        Console.WriteLine("Connection {0} - Sent {1} bytes to server.", clientId, bytesSent);

        // and get the response
        resultSocket.customSocket.BeginReceive(resultSocket.Buffer, 0, MySocket.Size, 0, OnReceive, resultSocket);
    }

    private static void OnReceive(IAsyncResult ar)
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
            Console.WriteLine(("Bytes read - {0}", bytesRead));

            if (bytesRead > 0)
            {
                resultSocket.Response.Append(Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead));
                clientSocket.BeginReceive(resultSocket.Buffer, 0, MySocket.Size, 0, OnReceive, resultSocket);
                Console.WriteLine("Content is - {0}", resultSocket.Response);
            }
            else
            {
                // save data
                if (resultSocket.Response.Length > 1)
                {
                    answer = resultSocket.Response.ToString();
                }
                Console.WriteLine("Content is - {0}", resultSocket.Response);
                clientSocket.Shutdown(SocketShutdown.Both); // Disables sends and receives on clientSocket
                clientSocket.Close(); // Close clientSocket connection and releases all resources
            }
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
}