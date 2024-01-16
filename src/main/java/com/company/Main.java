package com.company;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            String fileUrl = args[0];
            long startTime = System.currentTimeMillis();
            try {
                URL url = new URL(fileUrl);
                InputStream fileStream = url.openStream();
                if (fileUrl.endsWith(".gz")) {
                    fileStream = new GZIPInputStream(fileStream);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
                Map<String, List<String>> groups = new HashMap<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.matches("\".*\".*\".*\"")) {
                        String[] columns = line.split(";");
                        String key = String.join(";", Arrays.copyOfRange(columns, 1, columns.length));
                        List<String> group = groups.getOrDefault(key, new ArrayList<>());
                        group.add(line);
                        groups.put(key, group);
                    }
                }
                List<List<String>> result = new ArrayList<>(groups.values());
                Collections.sort(result, (list1, list2) -> Integer.compare(list2.size(), list1.size()));
                int count = 0;
                try (Writer writer = new FileWriter("output.txt")) {
                    for (List<String> group : result) {
                        if (group.size() > 1) {
                            count++;
                            writer.write("Группа " + count + "\n");
                            Set<String> uniqueLines = new HashSet<>(group);
                            for (String uniqueLine : uniqueLines) {
                                writer.write(uniqueLine + "\n");
                            }
                            writer.write("\n");
                        }
                    }
                    writer.flush();
                }
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                System.out.println("Количество полученных групп с более чем одним элементом: " + count);
                System.out.println("Время выполнения программы: " + executionTime + " мс");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Пожалуйста, укажите URL файла в качестве аргумента командной строки");
        }
    }
}
