

/*
 * 1. 将整个数组构建成一棵二叉树，理解 buildHeap 部分
 * 2. heapify 部分主要就是递归的思想。 无它
 *
 *
 */

public class HeapSort {

    boolean debug = true;

    void swap(int A[], int i, int j) {
        int tmp = A[i];
        A[i] = A[j];
        A[j] = tmp;
    }

    void heapify(int A[], int i, int size) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int max = i;
        if (left < size && A[left] > A[max]) {
            max = left;
        }
        if (right < size && A[right] > A[max]) {
            max = right;
        }
        if (debug) {
            if (right < size) {
                System.out.println(String.format("root=%d left=%d right=%d", A[i], A[left], A[right]));
            } else if (left < size) {
                System.out.println(String.format("root=%d left=%d right=null", A[i], A[left]));
            } else {
                System.out.println(String.format("root=%d left=null right=null", A[i]));
            }
        }
        if (max != i) {
            if (debug) {
                System.out.println(String.format("swap %d, %d", A[i], A[max]));
            }
            swap(A, i, max);
            heapify(A, max, size);
        }
    }

    void dumpHeap(int A[], int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(A[i]);
        }
    }

    void buildHeap(int A[], int n) {
        System.out.println("before ");
        dumpHeap(A, n);
        for (int i = n/2 - 1; i >= 0; i--) {
            heapify(A, i, n);
        }
        System.out.println("after ");
        dumpHeap(A, n);
    }

    public static void sort(int A[], int n) {
        HeapSort heap = new HeapSort();
        heap.buildHeap(A, n);
        while (n > 1) {
            heap.swap(A, 0, --n);
            heap.heapify(A, 0, n);
        }
        System.out.println("sort ...");
        heap.dumpHeap(A, 8);
    }

    public static void main(String args[]) {
        int A[] = {3, 4, 5, 2, 10, 7 , 8, 6};
        sort(A, 8);
    }
}
