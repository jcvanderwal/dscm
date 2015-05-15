package org.estatio.dscm.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandService {

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

}