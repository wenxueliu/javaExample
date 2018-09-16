
public class MergeSort {

    boolean debug = false;

    void merge(int A[], int left, int mid, int right) {
        //check (left <= mid); (mid <= right);
        if (left == right) return;
        int len = right - left + 1;
        int []tmp = new int[len];
        int pos = mid + 1;
        int i = 0;
        int begin = left;
        for (i = 0; i < len; i++) {
            if (debug) {
                System.out.println(String.format("left=%d right=%d ", A[left <= mid ? left : left - 1], A[pos <= right ? pos : pos -1]));
            }
            if (left <= mid && pos <= right) {
                if (A[left] < A[pos]) {
                    tmp[i] = A[left];
                    left++;
                } else {
                    tmp[i] = A[pos];
                    pos++;
                }
            } else {
                if (left <= mid) {
                    tmp[i] = A[left];
                    left++;
                }
                if (pos <= right) {
                    tmp[i] = A[pos];
                    pos++;
                }
            }
        }
        for (int j = 0; j < len; j++) {
            A[begin] = tmp[j];
            begin++;
        }
    }

    static void dumpArray(int A[], int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(A[i]);
        }
    }

    void mergeSort(int A[], int left, int right) {
        //循环终止条件
        if (left == right) return;
        int mid = left + (right - left) / 2;
        mergeSort(A, left, mid);
        mergeSort(A, mid+1, right);
        merge(A, left, mid, right);
    }

    void mergeSortIteration(int A[], int len) {
        int left, mid, right;
        for (int i = 1; i < len; i *= 2) {
            left = 0;
            while (left + i < len) {
                // 数组的  i初始为1，每轮翻倍
                // 后 个 数组存在(需要归并)
                mid = left + i - 1;
                right = mid + i < len ? mid + i : len - 1;// 后 个 数组  可能 够 Merge(A, left, mid, right);
                merge(A, left, mid, right);
                left = right + 1;
            }
        }
    }

    //非递归如何解决元素不整除是难点
    void mergeSort1(int A[], int left, int right) {
        int len = right - left + 1;
        int k = 1;
        while (k <= len) {
            System.out.println(String.format("k = %d", k));
            int i = 0;
            for (; i < len; i += 2*k) {
                //0, 1, 1
                int end = i+k*2-1;
                end = end > right ? right : end;
                int mid = i + k - 1;
                if (mid < end) {
                    System.out.println(String.format("merge %d %d %d", i, mid, end));
                    merge(A, i, mid, end);
                }
            }
            k = k * 2;
        }
    }

    static void sort() {
        int A[] = {1, 2, 5, 3, 4, 8, 3, 2, 5, 3, 4, 5, 5, 3};
        dumpArray(A, A.length);
        MergeSort merge = new MergeSort();
        merge.mergeSort1(A, 0, A.length - 1);
        //merge.mergeSortIteration(A, A.length);
        dumpArray(A, A.length);
    }

    public static void main(String args[]) {
        sort();
    }
}
