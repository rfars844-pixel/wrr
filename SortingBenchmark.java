import java.util.*;
import java.io.*;

public class SortingBenchmark {

    static Random random = new Random();

    interface Sorter {
        void sort(int[] a);
    }

    static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++)
                if (a[j] < a[min]) min = j;
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i], j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean changed = false;
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    changed = true;
                }
            }
            if (!changed) break;
        }
    }

    static void mergeSort(int[] a) {
        if (a.length < 2) return;
        int mid = a.length / 2;
        int[] left = Arrays.copyOfRange(a, 0, mid);
        int[] right = Arrays.copyOfRange(a, mid, a.length);
        mergeSort(left);
        mergeSort(right);
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length)
            a[k++] = left[i] <= right[j] ? left[i++] : right[j++];
        while (i < left.length) a[k++] = left[i++];
        while (j < right.length) a[k++] = right[j++];
    }

    static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;
        int pivot = a[low + (high - low) / 2];
        int i = low, j = high;
        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                int t = a[i]; a[i] = a[j]; a[j] = t;
                i++; j--;
            }
        }
        if (low < j) quickSort(a, low, j);
        if (i < high) quickSort(a, i, high);
    }

    static void radixSort(int[] a) {
        int[] output = new int[a.length];
        for (int shift = 0; shift < 32; shift += 8) {
            int[] count = new int[256];
            for (int value : a) {
                int key = value ^ Integer.MIN_VALUE;
                count[(key >>> shift) & 255]++;
            }
            for (int i = 1; i < 256; i++)
                count[i] += count[i - 1];

            for (int i = a.length - 1; i >= 0; i--) {
                int key = a[i] ^ Integer.MIN_VALUE;
                int digit = (key >>> shift) & 255;
                output[--count[digit]] = a[i];
            }
            System.arraycopy(output, 0, a, 0, a.length);
        }
    }

    static int[] generate(int n, String scenario) {
        int[] a = new int[n];

        for (int i = 0; i < n; i++)
            a[i] = random.nextInt(200001) - 100000;

        if (scenario.equals("Fully Sorted")) {
            Arrays.sort(a);
        } else if (scenario.equals("Reverse Sorted")) {
            Arrays.sort(a);
            for (int i = 0; i < n / 2; i++) {
                int t = a[i];
                a[i] = a[n - 1 - i];
                a[n - 1 - i] = t;
            }
        } else if (scenario.equals("Partially Sorted")) {
            Arrays.sort(a);
            for (int i = (int)(n * 0.8); i < n; i++)
                a[i] = random.nextInt(200001) - 100000;
        }

        return a;
    }

    public static void main(String[] args) {
        int n = 100000;
        if (args.length > 0) {
            try {
                n = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid size. Using 100000.");
            }
        }

        String[] scenarios = {
            "Random", "Reverse Sorted",
            "Partially Sorted", "Fully Sorted"
        };

        String[] names = {
            "Selection Sort", "Insertion Sort", "Bubble Sort",
            "Merge Sort", "Quick Sort", "Radix Sort"
        };

        Sorter[] algorithms = {
            SortingBenchmark::selectionSort,
            SortingBenchmark::insertionSort,
            SortingBenchmark::bubbleSort,
            SortingBenchmark::mergeSort,
            SortingBenchmark::quickSort,
            SortingBenchmark::radixSort
        };

        try (PrintWriter csv = new PrintWriter(
                new FileWriter("benchmark_results.csv"))) {

            csv.println("Scenario,Algorithm,Size,Time Microseconds,Sorted");

            for (String scenario : scenarios) {
                int[] original = generate(n, scenario);

                System.out.println("\nScenario: " + scenario);
                System.out.println("--------------------------------");

                for (int i = 0; i < algorithms.length; i++) {
                    int[] data = original.clone();

                    long start = System.nanoTime();
                    algorithms[i].sort(data);
                    long end = System.nanoTime();

                    double microseconds = (end - start) / 1000.0;
                    boolean sorted = true;

                    for (int j = 1; j < data.length; j++) {
                        if (data[j] < data[j - 1]) {
                            sorted = false;
                            break;
                        }
                    }

                    System.out.printf(
                        "%-18s : %.2f microseconds | Sorted: %s%n",
                        names[i], microseconds, sorted
                    );

                    csv.printf("%s,%s,%d,%.2f,%s%n",
                        scenario, names[i], n, microseconds, sorted);
                }
            }

            System.out.println("\nResults saved to benchmark_results.csv");

        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
        }
    }
}