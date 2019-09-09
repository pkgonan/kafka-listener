# Runtime kafka listener


## Purpose
* Provides new topic listening at runtime.
* Spring-kafka's @KafkaListener not provide new topic listening at runtime.


## How to use ?
* See KafkaListener.java


## Try this !
* Clone project


        git clone https://github.com/pkgonan/kafka-listener.git


* Initialize


        docker-compose up -d
        
        
* New topic listener registration at runtime
        
        
        curl -XPOST localhost:8080/consumers/order/register -H "Content-Type: application/json" -d '{ "topics" : ["Topic1", "Topic2"] }'
    
    
* New topic listener de-registration at runtime
        
        
        curl -XPOST localhost:8080/consumers/order/de-register -H "Content-Type: application/json" -d '{ "topics" : ["Topic1"] }'
    
    
* Topic listener stop at runtime
        
        
        curl -XPOST localhost:8080/consumers/order/stop -H "Content-Type: application/json"
    
    
* Topic listener start at runtime


        curl -XPOST localhost:8080/consumers/order/start -H "Content-Type: application/json"

