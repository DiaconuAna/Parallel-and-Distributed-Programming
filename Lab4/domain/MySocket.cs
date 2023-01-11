using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace ConsoleApp1.domain;

public class MySocket
{
    public readonly ManualResetEvent ConnectDone = new(false);
    public readonly ManualResetEvent SendDone = new(false);
    public readonly ManualResetEvent ReceiveDone = new(false);
    
    public int Id;
    public string Hostname;
    public string Endpoint;
    public IPEndPoint RemoteIpEndPoint;

    public const int Size = 1024;
    public readonly byte[] Buffer = new byte[Size];
    public readonly StringBuilder Response = new();

    public Socket customSocket = null;
}