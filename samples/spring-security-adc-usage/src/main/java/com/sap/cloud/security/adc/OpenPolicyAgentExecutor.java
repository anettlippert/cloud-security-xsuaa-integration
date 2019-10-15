package com.sap.cloud.security.adc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;

public class OpenPolicyAgentExecutor {
    private static OpenPolicyAgentExecutor _exec = null;
    private Process opaProcess;
    private HttpClient client;

    public static OpenPolicyAgentExecutor get() {
        if (_exec == null) {
            _exec = new OpenPolicyAgentExecutor();
        }
        return _exec;

    }

    private OpenPolicyAgentExecutor() {
        client = HttpClientBuilder.create().build();
    }


    public void start() throws Exception {
        opaProcess = Runtime.getRuntime().exec("/home/vcap/app/opa run -s /home/vcap/app/BOOT-INF/classes/data.json /home/vcap/app/BOOT-INF/classes/rbac.rego");
        new Thread(new DumpInputRunnable(opaProcess, opaProcess.getInputStream())).start();
        new Thread(new DumpInputRunnable(opaProcess, opaProcess.getErrorStream())).start();
        return;
    }

    public void stop() throws Exception {
        opaProcess = opaProcess.destroyForcibly();
        new Thread(new DumpInputRunnable(opaProcess, opaProcess.getInputStream())).start();
        new Thread(new DumpInputRunnable(opaProcess, opaProcess.getErrorStream())).start();
        return;
    }

    public String getVersion() throws Exception {
        Process process = Runtime.getRuntime().exec("/home/vcap/app/opa version");
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            throw new OpenPolicyEngineException(IOUtils.toString(process.getErrorStream()));
        }
        return IOUtils.toString(process.getInputStream());
    }

    public boolean ping() throws IOException {
        HttpGet request = new HttpGet("http://localhost:8181/v1/policies");
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (HttpHostConnectException e) {
            return false;
        }

        return response.getStatusLine().getStatusCode() == 200;
    }

    private class DumpInputRunnable implements Runnable {
        Process p;
        InputStream is;

        DumpInputRunnable(Process p, InputStream is) {
            this.p = p;
            this.is = is;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (p.isAlive()) {
                try {
                    String s = reader.readLine();
                    System.out.println(s);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
