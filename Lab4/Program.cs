using System;
using System.Linq;
using System.Runtime.InteropServices;
using ConsoleApp1.solutions;

namespace ConsoleApp1
{
    internal static class Program
    {
        private static void Main(string[] args)
        {
            var hosts = new [] {"www.cs.ubbcluj.ro/~rlupsa/edu/pdp/", "www.cs.ubbcluj.ro/~forest"}.ToList();
            // CallbackSolution.Run(hosts);
            // Console.WriteLine(CallbackSolution.answer);
            Console.WriteLine("**************************");
            // SyncTaskSolution.Run(hosts);
            // Console.WriteLine(SyncTaskSolution.answer);
            Console.WriteLine("**************************");
            AsyncTaskSolution.Run(hosts);
            Console.WriteLine(AsyncTaskSolution.answer);
        }
    }
}