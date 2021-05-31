package com.typlistic.app.controller;

import com.typlistic.app.model.Type;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
public class NERController {
     @Autowired
    private StanfordCoreNLP stanfordCoreNLP;
    @PostMapping
    @RequestMapping(value="/ner")
    public Set<String> ner(@RequestBody final String input, @RequestParam final Type type){
        CoreDocument coreDocument = new CoreDocument(input);
        stanfordCoreNLP.annotate((coreDocument));
        List<String> myCollectionsList = new ArrayList<>();
        List<String> myClassificationList = new ArrayList<>();
        List<CoreLabel> corelabels = coreDocument.tokens();
        for (CoreLabel coreLabel : corelabels)
        {
            String keys = coreLabel.originalText();
            String lemma = coreLabel.lemma();
            String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            String labelSummary = coreLabel.originalText()+": pos:"+pos+" lemma:"+lemma+ " NER:"+ner;
            myClassificationList.add(labelSummary);
            System.out.println(labelSummary);
        }
        int tcounter=0;
        for (CoreLabel coreLabel : corelabels)
        {
            tcounter= tcounter+1;
        }
        List<CoreSentence> sentences = coreDocument.sentences();
        int scounter = 0;
        int osentiment = 0;
        for (CoreSentence sentence : sentences) {
            String sentiment = sentence.sentiment();
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
        String paramType = type.toString();
        String ifParam = "OTHER";
        System.out.println(paramType+" "+ifParam+" "+paramType.equals(ifParam));

        if( paramType.equals(ifParam))
        {
            System.out.println("In Other");
            myCollectionsList = myClassificationList;
        }
        else
        {
            myCollectionsList = collectList(corelabels, type);
        }
        myCollectionsList.add("Total Word Count: "+ tcounter);
        myCollectionsList.add("Total Sentence Count: "+ scounter);
        myCollectionsList.add("Overall Sentiment: "+ overallSentiment);
        return new HashSet<>(myCollectionsList);
    }

    private List<String> collectList(List<CoreLabel> coreLabels, final Type type) {
            return coreLabels
                    .stream()
                    .filter(coreLabel -> type.getName().equalsIgnoreCase(coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class)))
                    .map(CoreLabel::originalText)
                    .collect(Collectors.toList());

    }


}
