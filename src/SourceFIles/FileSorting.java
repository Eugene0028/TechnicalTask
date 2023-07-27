import java.io.*;
import java.util.*;
import java.util.List;

public class FileSorting
{
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB
    private static String type;
    private static String regime;

    FileSorting(ArrayList<String> InputNames)
    {
        type = Main.isString ? "-s" : "-i";
        regime = Main.isDescending ? "-d" : "-a";

        List<File> chunks = null;
        List<File> sortedChunks = null;
        try
        {
            AllFilesInOne("temp\\MergeAllFiles", InputNames);
            chunks = splitFile("temp\\MergeAllFiles");// Разделение файла на чанки
            sortedChunks = sortChunks(chunks);// Сортировка и запись каждого чанка во временные файлы
            MergeSortInTempFiles(sortedChunks, "output.txt");// Слияние всех отсортированных частей в итоговый файл
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            assert chunks != null;
            cleanupTempFiles(chunks);
            assert sortedChunks != null;
            cleanupTempFiles(sortedChunks);
        }

    }
    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
        public static void AllFilesInOne(String outputFile, ArrayList<String> inputFiles)
        {
            try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                for (String inputFile : inputFiles) {
                    try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(inputFile)))
                    {
                        int bytesRead;
                        byte[] buffer = new byte[1024];
                        int i = 0;
                        while ((bytesRead = fis.read(buffer)) != -1)
                        {
                            fos.write(buffer, 0, bytesRead);
                            i+= bytesRead;
                        }
                        fos.write('\n');
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private List<File> splitFile(String OutputFileName) throws IOException {
        List<File> chunks = new ArrayList<>();
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;
        int chunkNum = 0;
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(OutputFileName)))
            {
                while ((bytesRead = inputStream.read(buffer)) > 0)
                {
                    File chunk = new File("temp\\chunk" + chunkNum + ".tmp");
                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(chunk)))
                    {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    chunks.add(chunk);
                    chunkNum++;
                }
            }
        return chunks;
    }
    
    private List<File> sortChunks(List<File> chunks) throws Exception {

        List<File> sortedChunks = new ArrayList<>();
        
        for (File chunk : chunks)
        {
            List<Object> objects = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(chunk))) {
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    try
                    {
                        if (!line.isBlank() && type.equals("-i") && isNumeric(line)) objects.add(Integer.parseInt(line.strip()));
                        else if (!line.isBlank() && type.equals("-s") && !isNumeric(line)) objects.add(line.strip());
                        else throw new Exception("Error. Please, enter valid input\nIf your flag is '-s' - enter String\nIf your flag is '-i' - enter Integer\n");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            MergeSortIterative.MergeObjectsInFile(objects, type);

            File sortedChunk = new File("temp\\", chunk.getName().replace(".tmp", "sorted.tmp"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sortedChunk))) 
            {
                for (var obj : objects) 
                {
                    if (type.equals("-i"))writer.write(Integer.toString((Integer) obj));
                    else writer.write((String) obj);
                    writer.newLine();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            sortedChunks.add(sortedChunk);

        }
        return sortedChunks;
    }

    private void MergeSortInTempFiles(List<File> tempFiles, String outputFile) throws IOException
    {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile)))
        {
            PriorityQueue<LineWrapper> minHeap = new PriorityQueue<>();

            List<BufferedReader> readers = new ArrayList<>();
            for (File file : tempFiles) {
                readers.add(new BufferedReader(new FileReader(file)));
            }

            for (int i = 0; i < readers.size(); i++) {
                BufferedReader reader = readers.get(i);
                String line = reader.readLine();
                if (line != null) {
                    minHeap.offer(new LineWrapper(line, i));
                }
            }


            while (!minHeap.isEmpty()) {
                LineWrapper lineWrapper = minHeap.poll();
                writer.println(lineWrapper.line);

                BufferedReader reader = readers.get(lineWrapper.fileIndex);
                String nextLine = reader.readLine();
                if (nextLine != null) {
                    minHeap.offer(new LineWrapper(nextLine, lineWrapper.fileIndex));
                }
            }

            for (BufferedReader reader : readers) {
                reader.close();
            }
        }
    }

    public static class MergeSortIterative
    {
        public static void MergeObjectsInFile(List<Object> arr, String type) {
            int n = arr.size();
            boolean ascending = !regime.equals("-d");

            for (int currSize = 1; currSize <= n - 1; currSize = 2 * currSize) {
                for (int left = 0; left < n - 1; left += 2 * currSize) {
                    int mid = Math.min(left + currSize - 1, n - 1);
                    int right = Math.min(left + 2 * currSize - 1, n - 1);

                    merge(arr, left, mid, right, type, ascending);
                }
            }
        }

        private static void merge(List<Object> arr, int left, int mid, int right, String type, boolean ascending) {
            int n1 = mid - left + 1;
            int n2 = right - mid;

            List<Object> tempLeftArr = new ArrayList<>();
            List<Object> tempRightArr = new ArrayList<>();

            for (int i = 0; i < n1; i++) {
                tempLeftArr.add(arr.get(left + i));
            }
            for (int j = 0; j < n2; j++) {
                tempRightArr.add(arr.get(mid + 1 + j));
            }

            int i = 0, j = 0;
            int k = left;

            while (i < n1 && j < n2) {
                int compareResult = compare(type, tempLeftArr, tempRightArr, i, j);

                if (ascending) {
                    if (compareResult <= 0) {
                        arr.set(k, tempLeftArr.get(i));
                        i++;
                    } else {
                        arr.set(k, tempRightArr.get(j));
                        j++;
                    }
                } else {
                    if (compareResult >= 0) {
                        arr.set(k, tempLeftArr.get(i));
                        i++;
                    } else {
                        arr.set(k, tempRightArr.get(j));
                        j++;
                    }
                }
                k++;
            }

            while (i < n1) {
                arr.set(k, tempLeftArr.get(i));
                i++;
                k++;
            }

            while (j < n2) {
                arr.set(k, tempRightArr.get(j));
                j++;
                k++;
            }
        }
        private static int compare(String type, List<Object> arr1, List<Object> arr2, int i, int j )
        {
            if (type.equals("-i")) return Integer.compare((Integer)arr1.get(i), (Integer)arr2.get(j));
            return arr1.get(i).toString().compareTo(arr2.get(j).toString());
        }
    }

    static class LineWrapper implements Comparable<LineWrapper> {
        String line;
        int fileIndex;

        LineWrapper(String line, int fileIndex) {
            this.line = line;
            this.fileIndex = fileIndex;
        }

        @Override
        public int compareTo(LineWrapper other) {
            return this.line.compareTo(other.line);
        }
    }
    
    private static void cleanupTempFiles(List<File> files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}