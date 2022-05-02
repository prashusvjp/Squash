package com.example.squash.models.encode_decode;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// IMplementing the huffman algorithm
public class Huffman {

    private static ArrayList<Character> charArray = new ArrayList<Character>(0);
    private static ArrayList<Integer> charFreq = new ArrayList<Integer>(0);
    private static String encodedString = ""; // encoded binary
    private static String fstr = ""; // entire file in the form of string
    private static String mappingsStr = ""; // char space binary representation
    private static Map<Character, String> Hmap = new HashMap<Character, String>();
    private static String decodedString = "";
    private static int baseValue = 7;
    private Context context;

    public Huffman(Context context){
        this.context = context;
    }

    public static void printCode(HuffmanNode root, String s) {
        // if(root)
        if (root.left == null && root.right == null) {

            // System.out.println(root.c + "\t" + s);
            mappingsStr += root.c + " " + s + "\n";
            Hmap.put(root.c, s);
            return;
        }
        if (root.left != null)
            printCode(root.left, s + "0");
        if (root.right != null)
            printCode(root.right, s + "1");
    }

    public void readInputFromFile(Uri uri) throws FileNotFoundException, IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String str;
        while ((str = br.readLine()) != null) fstr += str + "\n";

        br.close();
        Map<Character, Integer> freq = new HashMap<Character, Integer>();

        for (int i = 0; i < fstr.length(); ++i) {
            Character letter = fstr.charAt(i);
            int curr_freq = freq.containsKey(letter) ? freq.get(letter) : 0;
            freq.put(fstr.charAt(i), curr_freq + 1);
        }

        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            charArray.add(entry.getKey());
            charFreq.add(entry.getValue());
        }

    }

    public void encodeFile(Uri uri) throws FileNotFoundException, IOException {
        readInputFromFile(uri);
        Toast.makeText(context, "finised reading", Toast.LENGTH_SHORT).show();
        int n = charArray.size();
        PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>(n, new ImplementComparator());

        for (int i = 0; i < n; i++) {
            HuffmanNode hn = new HuffmanNode();

            hn.c = charArray.get(i);
            hn.item = charFreq.get(i);

            hn.left = null;
            hn.right = null;

            q.add(hn);
        }

        HuffmanNode root = null;
        // tree creation, create encode structure
        while (q.size() > 1) {

            HuffmanNode x = q.peek();
            q.poll();

            HuffmanNode y = q.peek();
            q.poll();

            HuffmanNode f = new HuffmanNode();

            f.item = x.item + y.item;
            f.c = '-';
            f.left = x;
            f.right = y;
            root = f;

            q.add(f);
        }

        writeCharBinPairs(root, "mappings.txt");
        Toast.makeText(context, "finised mapping.txt", Toast.LENGTH_SHORT).show();
        printCode(root, "");
        createAndWriteEncodedString("encoded.txt");
        Toast.makeText(context, "finised encoded.txt", Toast.LENGTH_SHORT).show();

        //decodeFile("./encoded.txt", "./mappings.txt");
    }


    public void writeCharBinPairs(HuffmanNode root, String filename) throws IOException {
        File file = new File(context.getFilesDir(),filename);
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));
            printCode(root, "");
            bf.write(mappingsStr);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {
            }
        }
    }

    public void writeToFile(String filename, String data, boolean writetoBinary) throws IOException {

        File file = new File(context.getFilesDir(),filename);
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(data);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {
            }
        }
    }


    public void createAndWriteEncodedString(String binfilename) throws IOException {
        String binary_string = "";

        for (int i = 0; i < fstr.length(); ++i) {
            binary_string += Hmap.get(fstr.charAt(i));
            if (binary_string.length() >= baseValue) {
                String str2 = "0" + binary_string.substring(0, baseValue);
                char ch = (char) Integer.parseInt((str2), 2);
                encodedString += ch;
                binary_string = binary_string.substring(baseValue, binary_string.length());
            }
        }

        int padding = 0;
        while (binary_string.length() % baseValue != 0) {
            binary_string += "0";
            ++padding;
        }

        if (binary_string.length() > 0) {

            char ch = (char) (Integer.parseInt(("0" + binary_string), 2));
            encodedString += ch;
        }
        encodedString = Integer.toString(padding) + encodedString;

        writeToFile(binfilename, encodedString, true);
    }


    public void decodeFile(String binfilename, String mappingfname) throws FileNotFoundException, IOException {

        /* Constructing HMapReverse using info from mappings.txt */
        File file = new File(mappingfname);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<String, Character> HmapReverse = new HashMap<String, Character>();

        String str;
        while ((str = br.readLine()) != null) {
            Character ch;
            String binstr;
            if (str.length() == 0) {
                ch = '\n';
                str = br.readLine();
                if (str == null) break;
                binstr = str.substring(1, str.length());
            } else {
                ch = str.charAt(0);
                binstr = str.substring(2, str.length());
            }

            HmapReverse.put(binstr, ch);
        }
        br.close();

        String binStr = "";

        br = new BufferedReader((new FileReader(new File(binfilename))));
        int padding = Character.getNumericValue((char) br.read());
        int c = 0;

        while (c != -1) {
            Character ch = (char) c;
            int c2nd = br.read();

            str = getAsciiBinString(ch).substring(1);
            if (c2nd == -1) {
                str = str.substring(0, str.length() - padding);
            }

            binStr += str;


            boolean map_available = true;
            while (map_available == true) {
                map_available = false;
                for (int endi = 1; endi <= binStr.length(); ++endi) {
                    if (HmapReverse.containsKey(binStr.substring(0, endi))) {
                        decodedString += HmapReverse.get(binStr.substring(0, endi));
                        binStr = binStr.substring(endi);
                        map_available = true;
                        break;
                    }
                }
            }
            c = c2nd;
        }

        writeToFile("./decodedFile.txt", decodedString, false);
    }

    public static String getAsciiBinString(Character input) {
        String s = input.toString();
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (int j = 0; j < bytes.length; j++) {
            int val = bytes[j];
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append("");
        }

        return binary.toString();
    }

    public static byte[] getAsciiBytes(String input) {
        char[] c = input.toCharArray();
        byte[] b = new byte[c.length];
        for (int i = 0; i < c.length; i++)
            b[i] = (byte) (c[i] & 0x007F);

        return b;
    }

}
