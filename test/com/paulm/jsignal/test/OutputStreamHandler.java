package com.paulm.jsignal.test;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class OutputStreamHandler extends Handler
{
	private boolean closed = false;
	
	@Override
	public void close() throws SecurityException
	{
		flush();
		closed = true;
	}

	@Override
	public void flush()
	{
		System.out.flush();
	}

	@Override
	public void publish(LogRecord arg0)
	{
		if (!closed)
		{
			String msg = "[jsignal unit test log record published ("+arg0.getSequenceNumber()+")]"+"\n"
			+ "(Logger) " + arg0.getLoggerName() + "\n"
			+ "(Level) " + arg0.getLevel() + "\n"
			+ "(Message) " + arg0.getMessage()
			+ (arg0.getThrown() != null ? " " + arg0.getThrown().getClass().getName() : "") + "\n"
			+ "(Occured at) " + arg0.getSourceClassName() + "#" + arg0.getSourceMethodName() + "\n"
			+ "[End of log record("+arg0.getSequenceNumber()+")]";
			System.out.println(msg);
		}
	}
}
