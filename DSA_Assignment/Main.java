package DSA_Assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * DSA Assignment: Sorting and Searching Performance Analysis
 * Consolidated Main Class
 */
public class Main {
    private static final int ITERATIONS = 10;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== DSA Assignment: Final Performance Analysis ===");
        
        // Ensure directory exists or use relative path correctly
        try (FileWriter csvWriter = new FileWriter("DSA_Assignment/results.csv")) {
            csvWriter.append("Type,Size,Algorithm,AvgTime\n");

            // --- Part 1: Sorting Analysis (Random Data) ---
            runSortingExperiment(csvWriter, "Random", new int[]{1000, 4000, 6000});

            // --- Part 2: Sorting Analysis (Sorted Data - Best/Worst Case) ---
            runSortingExperiment(csvWriter, "Sorted", new int[]{1000, 4000, 6000});

            // --- Part 3: Searching Analysis ---
            runSearchingExperiment(csvWriter, new int[]{100, 200, 300, 400, 500});

            System.out.println("\n✅ Analysis Complete! Results saved to 'DSA_Assignment/results.csv'");
        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
            // Fallback for printing results if file fails
        }
    }

    // --- EXPERIMENT RUNNERS ---

    private static void runSortingExperiment(FileWriter writer, String dataType, int[] sizes) throws IOException {
        System.out.println("\n[Sorting Analysis: " + dataType + " Data]");
        System.out.printf("%-10s | %-12s | %-12s | %-12s\n", "Size", "Insertion", "Shell", "Quick");
        System.out.println("------------------------------------------------------------");

        for (int size : sizes) {
            long t1 = 0, t2 = 0, t3 = 0;

            for (int i = 0; i < ITERATIONS; i++) {
                int[] arr = dataType.equals("Random") ? generateRandom(size) : generateSorted(size);
                
                int[] a1 = arr.clone();
                int[] a2 = arr.clone();
                int[] a3 = arr.clone();

                t1 += measureTime(() -> insertionSort(a1));
                t2 += measureTime(() -> shellSort(a2));
                t3 += measureTime(() -> quickSort(a3));
            }

            // Convert to Average Microseconds (μs)
            double avg1 = (double) (t1 / ITERATIONS) / 1000.0;
            double avg2 = (double) (t2 / ITERATIONS) / 1000.0;
            double avg3 = (double) (t3 / ITERATIONS) / 1000.0;

            System.out.printf("%-10d | %-12.2f | %-12.2f | %-12.2f\n", size, avg1, avg2, avg3);
            
            if (writer != null) {
                writer.append(dataType + "," + size + ",Insertion," + avg1 + "\n");
                writer.append(dataType + "," + size + ",Shell," + avg2 + "\n");
                writer.append(dataType + "," + size + ",Quick," + avg3 + "\n");
            }
        }
    }

    private static void runSearchingExperiment(FileWriter writer, int[] sizes) throws IOException {
        System.out.println("\n[Searching Analysis]");
        System.out.printf("%-10s | %-15s | %-15s\n", "Size", "Sequential", "Binary");
        System.out.println("------------------------------------------------------");

        for (int size : sizes) {
            long tSeq = 0, tBin = 0;
            int[] arr = generateSorted(size);

            for (int i = 0; i < ITERATIONS; i++) {
                int target = arr[random.nextInt(size)]; // Successful search
                tSeq += measureTime(() -> sequentialSearch(arr, target));
                tBin += measureTime(() -> binarySearch(arr, target));
            }

            // Average Nanoseconds (ns)
            double avgSeq = (double) tSeq / ITERATIONS;
            double avgBin = (double) tBin / ITERATIONS;

            System.out.printf("%-10d | %-15.2f | %-15.2f\n", size, avgSeq, avgBin);
            if (writer != null) {
                writer.append("Search," + size + ",Sequential," + avgSeq + "\n");
                writer.append("Search," + size + ",Binary," + avgBin + "\n");
            }
        }
    }

    // --- TIMING HELPERS ---

    public static long measureTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }

    // --- ALGORITHMS ---

    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; ++i) {
            int key = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > key) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

    public static void shellSort(int[] arr) {
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                int temp = arr[i], j;
                for (j = i; j >= gap && arr[j - gap] > temp; j -= gap) arr[j] = arr[j - gap];
                arr[j] = temp;
            }
        }
    }

    public static void quickSort(int[] arr) { quickSortRec(arr, 0, arr.length - 1); }
    private static void quickSortRec(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSortRec(arr, low, pi - 1);
            quickSortRec(arr, pi + 1, high);
        }
    }
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high], i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) { i++; int t = arr[i]; arr[i] = arr[j]; arr[j] = t; }
        }
        int t = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = t;
        return i + 1;
    }

    public static int sequentialSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == target) return i;
        return -1;
    }

    public static int binarySearch(int[] arr, int target) {
        int l = 0, r = arr.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (arr[m] == target) return m;
            if (arr[m] < target) l = m + 1; else r = m - 1;
        }
        return -1;
    }

    // --- UTILS ---

    public static int[] generateRandom(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = random.nextInt(100000);
        return arr;
    }

    public static int[] generateSorted(int size) {
        int[] arr = generateRandom(size);
        Arrays.sort(arr);
        return arr;
    }
}
