package com.typlistic.app.controller;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.JSONWriter;
import com.typlistic.app.model.Type;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.JSONOutputter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

@RestController
@RequestMapping(value = "/api/v1")
public class TextController {
    @Autowired
    private StanfordCoreNLP stanfordCoreNLP;
    @PostMapping
    @RequestMapping(value="/atext")
    public String atext(@RequestBody final String input){
        CoreDocument coreDocument = new CoreDocument(input);
        stanfordCoreNLP.annotate((coreDocument));
        List<String> respList = new ArrayList<>();
        org.json.JSONObject jobj = new org.json.JSONObject();

        List<CoreLabel> corelabels = coreDocument.tokens();
        int tcounter = 0;
        for (CoreLabel coreLabel : corelabels)
        {
            tcounter= tcounter+1;
        }
         jobj.put("tokenCount", tcounter);
         respList.add("Total Word Count: "+ tcounter);

        List<CoreSentence> sentences = coreDocument.sentences();
        int scounter = 0;
        int osentiment = 0;
        for (CoreSentence sentence : sentences) {
            String sentiment = sentence.sentiment();
            // jobj.put("Sentiment "+scounter,sentiment);
            System.out.println(sentence+" Sentiment "+scounter+"\t"+sentiment);
            if (sentiment=="Neutral")
            {
                osentiment = osentiment+0;
            }
            else if (sentiment=="Negative"){
                osentiment= osentiment-1;
            }
            else {
                osentiment=osentiment+1;
            }
            scounter = scounter+1;
        }
          String overallSentiment = "";
          if(osentiment==0){
              overallSentiment = "Neutral";
          }
          if(osentiment<0){
            overallSentiment = "Negative";
          }
          if(osentiment>0){
            overallSentiment = "Positive";
          }
          jobj.put("SentenceCount", scounter);
          respList.add("Total Sentence Count: "+ scounter);
          jobj.put("OverallSentiment",overallSentiment+" ");
          respList.add("Overall Sentiment: "+ overallSentiment);
            for (CoreLabel coreLabel : corelabels)
            {
                String keys = coreLabel.originalText();
                String lemma = coreLabel.lemma();
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                jobj.put(coreLabel.originalText(), "pos="+pos+" lemma = "+lemma+ " NER= "+ner );
                respList.add( coreLabel.originalText()+": pos:"+pos+" lemma:"+lemma+ " NER:"+ner );
            }
            //return new HashSet<>(respList);
           return jobj.toString();
    }

}
