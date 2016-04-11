package com.platform.cubism.front.compression;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CompressionServletResponseWrapper extends HttpServletResponseWrapper {
    protected HttpServletResponse origResponse = null;
    protected ServletOutputStream stream = null;
    protected PrintWriter writer = null;
    protected int threshold = 0;
    protected String contentType = null;

    public CompressionServletResponseWrapper(HttpServletResponse response) {
        super(response);
        origResponse = response;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
        origResponse.setContentType(contentType);
    }

    public void setCompressionThreshold(int threshold) {
        this.threshold = threshold;
    }

    public ServletOutputStream createOutputStream() throws IOException {
        CompressionResponseStream compressedStream = new CompressionResponseStream(origResponse);
        compressedStream.setBuffer(threshold);

        return compressedStream;
    }

    public void finishResponse() {
        try {
            if (writer != null) {
                writer.close();
            } else {
                if (stream != null)
                    stream.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        ((CompressionResponseStream)stream).flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null)
            throw new IllegalStateException("getWriter() has already been called for this response");
        if (stream == null)
            stream = createOutputStream();
        return (stream);
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        if (writer != null)
            return (writer);

        if (stream != null)
            throw new IllegalStateException("getOutputStream() has already been called for this response");

        stream = createOutputStream();
        String charEnc = origResponse.getCharacterEncoding();
        if (charEnc != null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
        } else {
            writer = new PrintWriter(stream);
        }
        
        return (writer);
    }

    @Override
    public void setContentLength(int length) {
        // Don't, as compression will change it
    }
}