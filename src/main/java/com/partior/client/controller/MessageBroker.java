package com.partior.client.controller;

import com.partior.client.dto.RtgsIntegration;
import com.vaadin.flow.component.page.Push;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.security.PermitAll;


@RestController
@RequestMapping("/ui")
@PermitAll
public class MessageBroker {


    private final UnicastProcessor<RtgsIntegration> rtgsStatusPublisher;


    private  final Flux<RtgsIntegration> rtgsStatusMessages;


    @Autowired
    public MessageBroker(UnicastProcessor<RtgsIntegration> rtgsStatusPublisher, Flux<RtgsIntegration> rtgsStatusMessages) {
        this.rtgsStatusPublisher = rtgsStatusPublisher;
        this.rtgsStatusMessages = rtgsStatusMessages;
        subscribe();
    }


    public void postXXX(

    ) {
//        this.rtgsStatusPublisher = rtgsStatusPublisher;
//        this.rtgsStatusMessages = rtgsStatusMessages;
//
//        subscribe();
    }

    @GetMapping(value = "/sendmessage")
    public ResponseEntity<String> sendRtgsStatus(){
        rtgsStatusPublisher.onNext(new RtgsIntegration("AAA","BBBB","CCC","DDDDD"));
        return new ResponseEntity<>("1", HttpStatus.OK);
    }

    private void subscribe(){
        System.out.println("Mess Broker rtgsStatusMessages:" + rtgsStatusMessages.hashCode());
        rtgsStatusMessages.subscribe();
    }
}
