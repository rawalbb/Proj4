import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.IntStream;

public class Proj4 {
    public static void main(String[] args) throws IOException {
        ArrayList<String[]> tokenizedSentences = readSentences();

        ArrayList<ArrayList<String>> minedSentences = mineText(tokenizedSentences);

        ArrayList<HashMap<String, Double>> vector = generateTermDocumentMatrix(minedSentences);
        ArrayList<HashMap<String, Double>> normalizedVect = normalizeVector(vector);
        HashMap<HashMap<String, Double>, ArrayList<Double>> weightedVectors = generateWeights(normalizedVect);
        ArrayList<Double> allNets = normalizeWeights(weightedVectors);
        reWeights(weightedVectors, allNets);
        //wta_clustering(vector, 10);
        int count = 0;
        while (count!=1){
            HashMap<HashMap<String, Double>, ArrayList<Double>> a = reWeights(weightedVectors, allNets);
            allNets = normalizeWeights(a);
            for (double c  :allNets
                 ) { System.out.println(c);

            }            count++;
        }
    }

    public static ArrayList<String[]> readSentences() throws IOException {
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

    public static ArrayList<ArrayList<String>> mineText(ArrayList<String[]> sentences) throws IOException {
        // remove punctuation, special characters & numbers (#1 B & C) -- DONE
        // covert upper to lowercase (#1 D) -- DONE
        // remove stop words (#1 E) -- DONE
        // perform stemming & combine stemmed words (#1 F & G)) -- DONE
        ArrayList<ArrayList<String>> minedSentences = new ArrayList<>();
        ArrayList<String> stopWords = readInStopWords();

        for (String[] sentence : sentences) {
            ArrayList<String> minedSentence = new ArrayList<>();
            for (String word : sentence) {
                Stemmer stemmer = new Stemmer();
                String minedWord;
                minedWord = word.toLowerCase();
                minedWord = minedWord.replaceAll("[^a-z]", "");
                if (!minedWord.equals("") && !stopWords.contains(minedWord)) {
                    Arrays.stream(minedWord.split("")).forEach(c -> stemmer.add(c.toCharArray()[0]));
                    stemmer.stem();
                    minedSentence.add(stemmer.toString());
                }
            }
            minedSentences.add(minedSentence);
        }

        return minedSentences;
    }

    public static ArrayList<String> readInStopWords() throws IOException {
        ArrayList<String> stopWords = new ArrayList<>();

        File file1 = new File("stop_words.txt");
        Scanner scanFile = new Scanner(file1);

        while (scanFile.hasNextLine()) {
            stopWords.add(scanFile.nextLine());
        }
        scanFile.close();
        return stopWords;
    }

    public static ArrayList<HashMap<String, Double>> generateTermDocumentMatrix(ArrayList<ArrayList<String>> minedSentences) throws IOException {
        HashSet<String> allTerms = new HashSet<>();
        ArrayList<HashMap<String, Double>> allTermSentenceMatrices = new ArrayList<>();
        for (ArrayList<String> sentence : minedSentences) {
            HashMap<String, Double> termSentenceMatrix = new HashMap<>();
            // counts number of times a word occurs in a sentence
            for (String word : sentence) {
                if (termSentenceMatrix.containsKey(word)) {
                    termSentenceMatrix.put(word, termSentenceMatrix.get(word) + 1);
                } else {
                    termSentenceMatrix.put(word, 1.0);
                }

                // accumulates a total word set
                if (!allTerms.contains(word)) {
                    allTerms.add(word);
                }
            }
            allTermSentenceMatrices.add(termSentenceMatrix);
        }

        // generates a csv of TDM
        PrintWriter csvWriter = new PrintWriter("TDM.csv", "UTF-8");

        csvWriter.print("Keyword set");
        for (String term : allTerms) {
            csvWriter.print("," + term);
        }
        csvWriter.println();

        //prints out a matrix like grid of all terms & word count in each sentence
        double sentenceCount = 1;
        for (HashMap<String, Double> sentence : allTermSentenceMatrices) {
            csvWriter.print("Sentence " + sentenceCount);
            for (String term : allTerms) {
                double count = 0;
                if (sentence.containsKey(term)) {
                    count = sentence.get(term);
                }
                csvWriter.print("," + count);
            }
            csvWriter.println();
            sentenceCount++;
        }
        csvWriter.close();

        return allTermSentenceMatrices;
    }

    public static ArrayList<HashMap<String, Double>> normalizeVector(ArrayList<HashMap<String, Double>> set) {
        // System.out.println("IN NORMALIZED VECTOR____________________");
        for (HashMap<String, Double> entry : set) {
            double sum = 0;
            for (double v : entry.values()) {
                sum += Math.pow(v, 2);
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
                weights.add(Math.random() * 5);
            }
            objectWeights.put(entry, weights);
        }
        System.out.println();
        //System.out.println("IN OBJECT WEIGHTS")
        //System.out.println(objectWeights);
        return objectWeights;
    }

    public static ArrayList<Double> normalizeWeights(HashMap<HashMap<String, Double>, ArrayList<Double>> o) {
        Iterator it = o.entrySet().iterator();
        ArrayList<Double> allNets = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Double> a = (ArrayList<Double>) pair.getValue();
            int sum = 0;
            for (int x = 0; x < a.size(); x++) {
                sum += Math.pow(a.get(x), 2);
            }
            ArrayList<Double> normalWeights = new ArrayList<>();
            HashMap<String, Double> aa = (HashMap<String, Double>) pair.getKey();
            Iterator throughKeyset= aa.keySet().iterator();
            int count = 0;
            double net = 0;
            while(throughKeyset.hasNext()) {
                double normalizedWeight = Math.sqrt(a.get(count) / sum);
                String keyword = throughKeyset.next().toString();
                System.out.print("net: " + net + "normalized weight: " + normalizedWeight + "freq: " + aa.get(keyword));
                net += normalizedWeight * aa.get(keyword);
                System.out.println(" new net: " + net);
                normalWeights.add(Math.sqrt(a.get(count) / sum));

                count++;
            }
            allNets.add(net);

            System.out.println("\n\nOVERALL NET FOR SENTENCE" + net + "\n\n");

            o.put((HashMap<String, Double>)pair.getKey(), normalWeights);
            System.out.println(pair.getKey() + "\n" + normalWeights);
        }
        return allNets;
    }

    public static HashMap<HashMap<String, Double>, ArrayList<Double>> reWeights(HashMap<HashMap<String, Double>, ArrayList<Double>> o, ArrayList<Double> allNets) {
        double maxNet = allNets.stream().mapToDouble(d -> d).max().orElseThrow(NoSuchElementException::new);
        double maxIndex = allNets.indexOf(maxNet);
        System.out.println("max net "+ maxNet);
        System.out.println("max index " + maxIndex);

        Iterator vals= o.entrySet().iterator();
        HashMap<String, Double> hashSentence;
        int hashMapCount = 0;
        while(vals.hasNext()) {
            Map.Entry pair = (Map.Entry) vals.next();
            if(maxIndex == 0 || (maxIndex > 0 && maxIndex == hashMapCount) ) {
                hashSentence = (HashMap<String, Double>) pair.getKey();
                Iterator abc = hashSentence.values().iterator();
                int abcCount = 0;
                ArrayList<Double> changeWeights= new ArrayList<>();
                while(abc.hasNext())
                {
//                    System.out.println("\n\n"+o.get(hashSentence));
                    double x = o.get(hashSentence).get(abcCount);
                    System.out.println("WHAT IS THIS:   " + o.get(hashSentence).get(abcCount));
                    double delta =  x + (double) abc.next()*.000003;
                    //System.out.println("COUNT " + abcCount);
                    //System.out.println("Weights Before " + o.get(hashSentence).get(abcCount) + "    Const " + ((double)abc.next()*.03));
                    changeWeights.add(delta + o.get(hashSentence).get(abcCount));
                    ++abcCount;
                }
                o.put(hashSentence, changeWeights);
                System.out.println("NEW WEIGHTS" + hashSentence + "\n" + changeWeights);
            }
            hashMapCount++;
        }
        return o;
    }

}

 