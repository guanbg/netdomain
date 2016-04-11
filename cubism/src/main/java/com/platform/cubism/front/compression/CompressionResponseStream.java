package com.platform.cubism.front.compression;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

public class CompressionResponseStream extends ServletOutputStream {
	protected int compressionThreshold = 0;
	protected byte[] buffer = null;
	protected int bufferCount = 0;
	protected boolean closed = false;
	protected int length = -1;
	protected OutputStream gzipstream = null;
	protected HttpServletResponse response = null;
	protected ServletOutputStream output = null;

	public CompressionResponseStream(HttpServletResponse response) throws IOException {
		super();
		closed = false;
		this.response = response;
		this.output = response.getOutputStream();
	}

	protected void setBuffer(int threshold) {
		compressionThreshold = threshold;
		buffer = new byte[compressionThreshold];
	}

	@Override
	public void close() throws IOException {
		if (closed)
			throw new IOException("This output stream has already been closed");

		if (gzipstream != null) {
			flushToGZip();
			gzipstream.close();
			gzipstream = null;
		} else {
			if (bufferCount > 0) {
				output.write(buffer, 0, bufferCount);
				bufferCount = 0;
			}
		}

		output.close();
		closed = true;
	}

	@Override
	public void flush() throws IOException {
		if (closed) {
			throw new IOException("Cannot flush a closed output stream");
		}

		if (gzipstream != null) {
			gzipstream.flush();
		}
	}

	public void flushToGZip() throws IOException {
		if (bufferCount > 0) {
			writeToGZip(buffer, 0, bufferCount);
			bufferCount = 0;
		}
	}

	@Override
	public void write(int b) throws IOException {
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		if (bufferCount >= buffer.length) {
			flushToGZip();
		}

		buffer[bufferCount++] = (byte) b;
	}

	@Override
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (len == 0) {
			return;
		}
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		// Can we write into buffer ?
		if (len <= (buffer.length - bufferCount)) {
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// There is not enough space in buffer. Flush it ...
		flushToGZip();

		// ... and try again. Note, that bufferCount = 0 here !
		if (len <= (buffer.length - bufferCount)) {
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// write direct to gzip
		writeToGZip(b, off, len);
	}

	public void writeToGZip(byte b[], int off, int len) throws IOException {
		if (gzipstream == null) {
			if (response.isCommitted()) {
				gzipstream = output;
			} else {
				response.addHeader("Content-Encoding", "gzip");
				String vary = response.getHeader("Vary");
				if (vary == null) {
					// Add a new Vary header
					response.setHeader("Vary", "Accept-Encoding");
				} else if (vary.equals("*")) {
					// No action required
				} else {
					// Merge into current header
					response.setHeader("Vary", vary + ",Accept-Encoding");
				}
				gzipstream = new GZIPOutputStream(output);
			}
		}
		gzipstream.write(b, off, len);

	}

	public boolean closed() {
		return (this.closed);
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		// TODO Auto-generated method stub
		
	}
}