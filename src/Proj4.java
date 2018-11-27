import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Proj4 {
    public static void main(String[] args) throws IOException{
        ArrayList<String[]> tokenizedSentences = readSentences();

        ArrayList<ArrayList<String>> minedSentences = mineText(tokenizedSentences);

        ArrayList<HashMap<String, Double>> vector = generateTermDocumentMatrix(minedSentences);
        ArrayList<HashMap<String, Double>> normalizedVect = normalizeVector(vector);
        generateWeights(normalizedVect);
        //wta_clustering(vector, 10);
    }

    public static ArrayList<String[]> readSentences() throws IOException{
        // tokenizes sentences into words (#1 A) -- DONE
        ArrayList<String[]> sentences = new ArrayList<>();

        File file1 = new File("sentences.txt");
        Scanner scanFile = new Scanner(file1);

        while (scanFile.hasNextLine()) {
            sentences.add(scanFile.nextLine().split(" "));
        }
        scanFile.close();
        return sentences;
    }

    public static ArrayList<ArrayList<String>> mineText(ArrayList<String[]> sentences) throws IOException{
        // remove punctuation, special characters & numbers (#1 B & C) -- DONE
        // covert upper to lowercase (#1 D) -- DONE
        // remove stop words (#1 E) -- DONE
        // perform stemming & combine stemmed words (#1 F & G)) -- DONE
        ArrayList<ArrayList<String>> minedSentences = new ArrayList<>();
        ArrayList<String> stopWords = readInStopWords();

        for(String[] sentence: sentences) {
            ArrayList<String> minedSentence = new ArrayList<>();
            for(String word: sentence) {
                Stemmer stemmer = new Stemmer();
                String minedWord;
                minedWord = word.toLowerCase();
                minedWord = minedWord.replaceAll("[^a-z]","");
                if(!minedWord.equals("") && !stopWords.contains(minedWord)) {
                    Arrays.stream(minedWord.split("")).forEach(c -> stemmer.add(c.toCharArray()[0]));
                    stemmer.stem();
                    minedSentence.add(stemmer.toString());
                }
            }
            minedSentences.add(minedSentence);
        }

        return minedSentences;
    }

    public static ArrayList<String> readInStopWords() throws IOException{
        ArrayList<String> stopWords = new ArrayList<>();

        File file1 = new File("stop_words.txt");
        Scanner scanFile = new Scanner(file1);

        while (scanFile.hasNextLine()) {
            stopWords.add(scanFile.nextLine());
        }
        scanFile.close();
        return stopWords;
    }

    public static  ArrayList<HashMap<String, Double>> generateTermDocumentMatrix(ArrayList<ArrayList<String>> minedSentences) throws IOException{
        HashSet<String> allTerms= new HashSet<>();
        ArrayList<HashMap<String, Double>> allTermSentenceMatrices= new ArrayList<>();
        for(ArrayList<String> sentence: minedSentences) {
            HashMap<String, Double> termSentenceMatrix= new HashMap<>();
            // counts number of times a word occurs in a sentence
            for(String word: sentence) {
                if(termSentenceMatrix.containsKey(word)) {
                    termSentenceMatrix.put(word, termSentenceMatrix.get(word) + 1);
                } else {
                    termSentenceMatrix.put(word, 1.0);
                }

                // accumulates a total word set
                if(!allTerms.contains(word)) {
                    allTerms.add(word);
                }
            }
            allTermSentenceMatrices.add(termSentenceMatrix);
        }

        // generates a csv of TDM
        PrintWriter csvWriter = new PrintWriter("TDM.csv", "UTF-8");

        csvWriter.print("Keyword set");
        for(String term: allTerms) {
            csvWriter.print("," +term);
        }
        csvWriter.println();

        //prints out a matrix like grid of all terms & word count in each sentence
        double sentenceCount = 1;
        for(HashMap<String, Double> sentence: allTermSentenceMatrices) {
            csvWriter.print("Sentence "+ sentenceCount);
            for(String term : allTerms) {
                double count = 0;
                if (sentence.containsKey(term)) {
                    count = sentence.get(term);
                }
                csvWriter.print("," +count);
            }
            csvWriter.println();
            sentenceCount++;
        }
        csvWriter.close();

        System.out.println("HASHMAP PRINT");

        System.out.println(allTermSentenceMatrices);
        return allTermSentenceMatrices;
    }

    public static ArrayList<HashMap<String, Double>> normalizeVector(ArrayList<HashMap<String, Double>> set)
    {
       // System.out.println("IN NORMALIZED VECTOR____________________");
        for (HashMap<String, Double> entry : set) {
            double sum = 0;
            for(double v :entry.values()) {
                sum += Math.pow(v,2);
            }
            for (String key : entry.keySet()) {
                double value = entry.get(key);
                value = value / Math.sqrt(sum);
                Double oldValue = entry.put(key, value);
             //   System.out.println("KEYY" + key + "VALUEE" + value);
            }
            //System.out.println("\n\nnew sentence\n\n");
        }
        //for each pattern do #occurunce for word/ sqrt(sum of all squared)
        //System.out.println();
        System.out.println("PRINTING SET NORMALIZED");
        System.out.println(set);
        return set;
    }

public static HashMap<HashMap<String, Double>, ArrayList<Double>> generateWeights(ArrayList<HashMap<String, Double>> set) {
    System.out.println("IN WEIGHTS");

    HashMap<HashMap<String, Double>, ArrayList<Double>> objectWeights = new HashMap<>();
    for (HashMap<String, Double> entry : set) {
        ArrayList<Double> weights = new ArrayList<>();
        for (int i = 0; i < entry.size(); i++) {
            weights.add(Math.random() * 10);
        }
        objectWeights.put(entry, weights);
    }
    System.out.println();
    //System.out.println("IN OBJECT WEIGHTS")
    System.out.println(objectWeights);
    return objectWeights;
}

public static double normalizeWeights(ArrayList<Double>)
{

    return 0;
}

}