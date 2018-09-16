

import java.util.Random;

class SortZoo {

    static Random rand = new Random();

    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        //a[i] = a[i] ^ a[j];
        //a[j] = a[i] ^ a[j];
        //a[i] = a[i] ^ a[j];
    }

    public static void qsort1(int[] a, int p, int r) {
        // 0个或1个元素，返回
        if (p >= r)
            return;
        // 选择左端点为pivot
        int x = a[p];
        int j = p;
        for (int i = p + 1; i <= r; i++) {
            // 小于pivot的放到左边
            if (a[i] < x) {
                swap(a, ++j, i);
            }
        }
        // 交换左端点和pivot位置
        swap(a, p, j);
        // 递归子序列
        qsort1(a, p, j - 1);
        qsort1(a, j + 1, r);
    }

    public static void qsort2(int[] a, int p, int r) {
        // 0个或1个元素，返回
        if (p >= r)
            return;
        // 随机选择pivot
        int i = p + rand.nextInt(r - p + 1);
        // 交换pivot和左端点
        swap(a, p, i);
        // 划分算法不变
        int x = a[p];
        int j = p;
        for (i = p + 1; i <= r; i++) {
            // 小于pivot的放到左边
            if (a[i] < x) {
                swap(a, ++j, i);
            }
        }
        // 交换左端点和pivot位置
        swap(a, p, j);
        // 递归子序列
        qsort2(a, p, j - 1);
        qsort2(a, j + 1, r);
    }

    public static void qsort3(int[] a, int p, int r) {
        if (p >= r)
            return;

        // 随机选
        int i = p + rand.nextInt(r - p + 1);
        swap(a, p, i);

        // 左索引i指向左端点
        i = p;
        // 右索引j初始指向右端点
        int j = r + 1;
        int x = a[p];
        while (true) {
            // 查找比x大于等于的位置
            do {
                i++;
            } while (i <= r && a[i] < x);
            // 查找比x小于等于的位置
            do {
                j--;
            } while (a[j] > x);
            if (j < i)
                break;
            // 交换a[i]和a[j]
            swap(a, i, j);
        }
        swap(a, p, j);
        qsort3(a, p, j - 1);
        qsort3(a, j + 1, r);
    }

    public static void qsort4(int[] a, int p, int r) {
        if (p >= r)
            return;

        // 在数组大小小于7的情况下使用快速排序
        if (r - p + 1 < 7) {
            for (int i = p; i <= r; i++) {
                for (int j = i; j > p && a[j - 1] > a[j]; j--) {
                    swap(a, j, j - 1);
                }
            }
            return;
        }

        int i = p + rand.nextInt(r - p + 1);
        swap(a, p, i);

        i = p;
        int j = r + 1;
        int x = a[p];
        while (true) {
            do {
                i++;
            } while (i <= r && a[i] < x);
            do {
                j--;
            } while (a[j] > x);
            if (j < i)
                break;
            swap(a, i, j);
        }
        swap(a, p, j);
        qsort4(a, p, j - 1);
        qsort4(a, j + 1, r);
    }

    public static void qsort5(int[] a, int p, int r) {
        if (p >= r)
            return;

        // 递归子序列，并且数组大小小于7，直接返回
        if (p != 0 && r!=(a.length-1) && r - p + 1 < 7)
            return;

        // 随机选
        int i = p + rand.nextInt(r - p + 1);
        swap(a, p, i);

        i = p;
        int j = r + 1;
        int x = a[p];
        while (true) {
            do {
                i++;
            } while (i <= r && a[i] < x);
            do {
                j--;
            } while (a[j] > x);
            if (j < i)
                break;
            swap(a, i, j);
        }
        swap(a, p, j);
        qsort5(a, p, j - 1);
        qsort5(a, j + 1, r);

        // 最后对整个数组进行插入排序
        if (p == 0 && r==a.length-1) {
            for (i = 0; i <= r; i++) {
                for (j = i; j > 0 && a[j - 1] > a[j]; j--) {
                    swap(a, j, j - 1);
                }
            }
            return;
        }
    }

    public static void qsort6(int[] a, int p, int r) {
        if (p >= r)
            return;

        // 在数组大小小于7的情况下使用快速排序
        if (r - p + 1 < 7) {
            for (int i = p; i <= r; i++) {
                for (int j = i; j > p && a[j - 1] > a[j]; j--) {
                    swap(a, j, j - 1);
                }
            }
            return;
        }

        // 计算数组长度
        int len = r - p + 1;
        // 求出中点，大小等于7的数组直接选择中数
        int m = p + (len >> 1);
        // 大小大于7
        if (len > 7) {
            int l = p;
            int n = p + len - 1;
            if (len > 40) { // 大数组，采用median-of-nine选择
                int s = len / 8;
                l = med3(a, l, l + s, l + 2 * s); // 取样左端点3个数并得出中数
                m = med3(a, m - s, m, m + s); // 取样中点3个数并得出中数
                n = med3(a, n - 2 * s, n - s, n); // 取样右端点3个数并得出中数
            }
            m = med3(a, l, m, n); // 取中数中的中数,median-of-three
        }
        // 交换pivot到左端点，后面的操作与qsort4相同
        swap(a, p, m);

        m = p;
        int j = r + 1;
        int x = a[p];
        while (true) {
            do {
                m++;
            } while (m <= r && a[m] < x);
            do {
                j--;
            } while (a[j] > x);
            if (j < m)
                break;
            swap(a, m, j);
        }
        swap(a, p, j);
        qsort6(a, p, j - 1);
        qsort6(a, j + 1, r);
    }

    private static int med3(int x[], int a, int b, int c) {
        return x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : x[b] > x[c] ? b : x[a] > x[c] ? c : a;
    }

    public static void qsort7(int[] x, int p, int r) {
        if (p >= r)
            return;

        // 在数组大小小于7的情况下使用快速排序
        if (r - p + 1 < 7) {
            for (int i = p; i <= r; i++) {
                for (int j = i; j > p && x[j - 1] > x[j]; j--) {
                    swap(x, j, j - 1);
                }
            }
            return;
        }

        // 选择中数，与qsort6相同。
        int len = r - p + 1;
        int m = p + (len >> 1);
        if (len > 7) {
            int l = p;
            int n = p + len - 1;
            if (len > 40) {
                int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n);
        }

        int v = x[m];

        // a,b进行左端扫描，c,d进行右端扫描
        int a = p, b = a, c = p + len - 1, d = c;
        while (true) {
            // 尝试找到大于pivot的元素
            while (b <= c && x[b] <= v) {
                // 与pivot相同的交换到左端
                if (x[b] == v)
                    swap(x, a++, b);
                b++;
            }
            // 尝试找到小于pivot的元素
            while (c >= b && x[c] >= v) {
                // 与pivot相同的交换到右端
                if (x[c] == v)
                    swap(x, c, d--);
                c--;
            }
            if (b > c)
                break;
            // 交换找到的元素
            swap(x, b++, c--);
        }

        // 将相同的元素交换到中间
        int s, n = p + len;
        s = Math.min(a - p, b - a);
        vecswap(x, p, b - s, s);
        s = Math.min(d - c, n - d - 1);
        vecswap(x, b, n - s, s);

        // 递归调用子序列
        if ((s = b - a) > 1)
            qsort7(x, p, s + p - 1);
        if ((s = d - c) > 1)
            qsort7(x, n - s, n - 1);
    }

    private static void vecswap(int x[], int a, int b, int n) {
        for (int i = 0; i < n; i++, a++, b++)
            swap(x, a, b);
    }

    public static void qsort(int a[], int begin, int end) {
        if (begin >= end) return;
        int k = begin + 1; //rand.nextInt(end - begin + 1);
        swap(a, begin, k);

        int pivot = a[begin];
        System.out.println(String.format("begin=%d, end=%d, pivot=%d", begin, end, pivot));

        int i = begin;
        int j = end + 1;
        while (true) {
            do {
                i++;
            } while (i <= end && a[i] < pivot);

            do {
                j--;
            } while (a[j] > pivot);

            if (j < i) {
                break;
            }
            swap(a, i, j);
        }

        swap(a, begin, j);
        int nbegin = j+1;
        int nend = j-1;

        qsort(a, begin, nend);
        qsort(a, nbegin, end);
    }

    public static void qsort8(int a[], int begin, int end) {
        if (begin >= end) return;
        int k = begin + 1; //rand.nextInt(end - begin + 1);
        swap(a, begin, k);

        int pivot = a[begin];
        System.out.println(String.format("begin=%d, end=%d, pivot=%d", begin, end, pivot));

        int i = begin;
        int j = end;
        while (true) {
            System.out.println(String.format("i=%d, j=%d", i, j));
            while (i < end && a[i] <= pivot) {
                i++;
            }
            while (j > begin && a[j] >= pivot) {
                j--;
            }

            if (j < i) {
                break;
            }
            swap(a, i, j);
        }

        swap(a, begin, j);
        int nbegin = j+1;
        int nend = j-1;

        qsort(a, begin, nend);
        qsort(a, nbegin, end);
    }

    public static void main(String args[]) {
        int a[] = {11, 10, 10, 10, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8};
        qsort8(a, 0, 15);
        for (int i = 0; i < 16; i++) {
            System.out.println(a[i]);
        }

        int b[] = {10, 10, 10};
        qsort8(b, 0, 2);
        for (int i = 0; i < 3; i++) {
            System.out.println(b[i]);
        }
    }
}


