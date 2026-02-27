package com.exam.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomForestService {

    public StudentAnalytics calculateStudentAnalytics(String studentId, List<String> answers, 
                                                      Map<Integer, String> answerKey, 
                                                      List<Long> timeTaken) {

        int totalQuestions = answerKey.size();
        int correctAnswers = calculateCorrectAnswers(answers, answerKey);
        double accuracy = (totalQuestions > 0) ? (correctAnswers * 100.0 / totalQuestions) : 0;

        Map<String, Double> topicScores = calculateTopicMastery(answers, answerKey);
        double avgTopicMastery = topicScores.values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);

        double difficultyResilience = calculateDifficultyResilience(answers, answerKey);

        double timeEfficiency = calculateTimeEfficiency(timeTaken);

        double confidence = calculateConfidence(answers, correctAnswers, totalQuestions);

        String performanceCategory = classifyPerformance(
            avgTopicMastery, difficultyResilience, accuracy, timeEfficiency, confidence
        );
        
        return new StudentAnalytics(
            studentId,
            avgTopicMastery,
            difficultyResilience,
            accuracy,
            timeEfficiency,
            confidence,
            performanceCategory
        );
    }
    
    private int calculateCorrectAnswers(List<String> answers, Map<Integer, String> key) {
        int correct = 0;
        for (int i = 0; i < answers.size(); i++) {
            if (key.containsKey(i + 1) && answers.get(i).equals(key.get(i + 1))) {
                correct++;
            }
        }
        return correct;
    }
    
    private Map<String, Double> calculateTopicMastery(List<String> answers, Map<Integer, String> key) {
        Map<String, Double> topicScores = new HashMap<>();

        int questionsPerTopic = Math.max(1, key.size() / 5);
        String[] topics = {"Mathematics", "Science", "History", "Logic", "General"};
        
        for (int i = 0; i < topics.length; i++) {
            int startIdx = i * questionsPerTopic;
            int endIdx = Math.min(startIdx + questionsPerTopic, answers.size());
            
            if (startIdx < answers.size()) {
                int correct = 0;
                int total = 0;
                
                for (int j = startIdx; j < endIdx && j < answers.size(); j++) {
                    if (key.containsKey(j + 1) && answers.get(j).equals(key.get(j + 1))) {
                        correct++;
                    }
                    total++;
                }
                
                double score = total > 0 ? (correct * 100.0 / total) : 0;
                topicScores.put(topics[i], score);
            }
        }
        
        return topicScores;
    }
    
    private double calculateDifficultyResilience(List<String> answers, Map<Integer, String> key) {
        int midpoint = answers.size() / 2;
        
        int easyCorrect = 0, hardCorrect = 0;
        int easyTotal = 0, hardTotal = 0;
        
        for (int i = 0; i < answers.size(); i++) {
            if (key.containsKey(i + 1) && answers.get(i).equals(key.get(i + 1))) {
                if (i < midpoint) easyCorrect++;
                else hardCorrect++;
            }
            
            if (i < midpoint) easyTotal++;
            else hardTotal++;
        }
        
        double easyScore = easyTotal > 0 ? (easyCorrect * 100.0 / easyTotal) : 0;
        double hardScore = hardTotal > 0 ? (hardCorrect * 100.0 / hardTotal) : 0;
        
        return hardScore > 0 ? (hardScore / Math.max(easyScore, 1)) * 100 : 0;
    }

    private double calculateTimeEfficiency(List<Long> timeTaken) {
        if (timeTaken == null || timeTaken.isEmpty()) return 75.0;
        
        double avgTime = timeTaken.stream().mapToLong(Long::longValue).average().orElse(60.0);
        double optimalTime = 45.0;

        double efficiency = 100 - Math.abs(avgTime - optimalTime) * 2;
        return Math.max(0, Math.min(100, efficiency));
    }

    private double calculateConfidence(List<String> answers, int correctAnswers, int totalQuestions) {
        double completionRate = answers.size() * 100.0 / totalQuestions;
        double accuracyRate = correctAnswers * 100.0 / Math.max(1, totalQuestions);
        
        return (completionRate * 0.3 + accuracyRate * 0.7);
    }

    private String classifyPerformance(double topicMastery, double resilience, 
                                      double accuracy, double timeEff, double confidence) {
        int excellentVotes = 0;
        int goodVotes = 0;
        int fairVotes = 0;
        int poorVotes = 0;
        
        if (accuracy >= 85) excellentVotes++;
        else if (accuracy >= 70) goodVotes++;
        else if (accuracy >= 50) fairVotes++;
        else poorVotes++;

        double consistency = (topicMastery + confidence) / 2;
        if (consistency >= 80) excellentVotes++;
        else if (consistency >= 65) goodVotes++;
        else if (consistency >= 45) fairVotes++;
        else poorVotes++;

        if (resilience >= 75 && timeEff >= 70) excellentVotes++;
        else if (resilience >= 60) goodVotes++;
        else if (resilience >= 40) fairVotes++;
        else poorVotes++;

        double overall = (topicMastery + resilience + accuracy + timeEff + confidence) / 5;
        if (overall >= 80) excellentVotes++;
        else if (overall >= 65) goodVotes++;
        else if (overall >= 45) fairVotes++;
        else poorVotes++;

        int maxVotes = Math.max(Math.max(excellentVotes, goodVotes), 
                               Math.max(fairVotes, poorVotes));
        
        if (maxVotes == excellentVotes) return "Excellent";
        if (maxVotes == goodVotes) return "Good";
        if (maxVotes == fairVotes) return "Fair";
        return "Needs Improvement";
    }
    
    public List<HistoricalPerformance> getHistoricalData(String studentId) {
        List<HistoricalPerformance> history = new ArrayList<>();
        
        Random rand = new Random();
        for (int i = 5; i >= 0; i--) {
            history.add(new HistoricalPerformance(
                "Exam " + (6 - i),
                70 + rand.nextInt(25)
            ));
        }
        
        return history;
    }
    
    public static class StudentAnalytics {
        public String studentId;
        public double topicMastery;
        public double difficultyResilience;
        public double accuracy;
        public double timeEfficiency;
        public double confidence;
        public String performanceCategory;
        
        public StudentAnalytics(String studentId, double tm, double dr, double acc, 
                              double te, double conf, String category) {
            this.studentId = studentId;
            this.topicMastery = tm;
            this.difficultyResilience = dr;
            this.accuracy = acc;
            this.timeEfficiency = te;
            this.confidence = conf;
            this.performanceCategory = category;
        }
        
        public String getStudentId() { return studentId; }
        public double getTopicMastery() { return topicMastery; }
        public double getDifficultyResilience() { return difficultyResilience; }
        public double getAccuracy() { return accuracy; }
        public double getTimeEfficiency() { return timeEfficiency; }
        public double getConfidence() { return confidence; }
        public String getPerformanceCategory() { return performanceCategory; }
    }
    
    public static class HistoricalPerformance {
        public String examName;
        public double score;
        
        public HistoricalPerformance(String examName, double score) {
            this.examName = examName;
            this.score = score;
        }
        
        public String getExamName() { return examName; }
        public double getScore() { return score; }
    }
}
