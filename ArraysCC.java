public class ArraysCC {
    public static int binarySearch(int numbers[], int key) {
        int start = 0, end = numbers.length - 1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if (numbers[mid] == key) {
                return mid;
            }
            if (numbers[mid] < key) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String args[]) {
        int numbers[] = { 2, 4, 6, 7, 8, 12, 24, 56 };
        int key = 24;
        System.out.println("index of key:" + binarySearch(numbers, key));
    }
}
