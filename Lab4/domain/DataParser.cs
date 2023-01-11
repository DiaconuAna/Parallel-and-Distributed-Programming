namespace ConsoleApp1.domain;

public class DataParser
{
    public static string GetRequestString(string hostname, string endpoint)
    {
        return "GET " + endpoint + " HTTP/1.1\r\n" +
               "Host: " + hostname + "\r\n" + 
               "Content-Length: 0\r\n\r\n";
    }
    
    public static int GetLengthContent(string response)
    {
        var contentLength = new int();
        foreach (var line in response.Split('\r', '\n'))
        {
            var headerDetails = line.Split(':');

            if (string.Compare(headerDetails[0], "Content-Length", StringComparison.Ordinal) == 0)
            {
                contentLength = int.Parse(headerDetails[1]);
            }
        }
        return contentLength;
    }

}