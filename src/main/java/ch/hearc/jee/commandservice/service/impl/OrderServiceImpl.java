package ch.hearc.jee.commandservice.service.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.hearc.jee.commandservice.model.Order;
import ch.hearc.jee.commandservice.model.OrderInProgressEvent;
import ch.hearc.jee.commandservice.model.OrderSubmitedEvent;
import ch.hearc.jee.commandservice.repository.OrderRepository;
import ch.hearc.jee.commandservice.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

	Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
	
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	JmsTemplate jmsTemplate;
	@Value("${spring.activemq.order.queue}")
    String queue;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	/**
	 * Listener sur commande in progress
	 * @param jsonMessage
	 * @throws JMSException
	 */
	@JmsListener(destination = "order-q")
    public void readInprogressMessage(final Message jsonMessage) throws JMSException {
        String messageData = null;
        System.out.println("Received command-q message " + jsonMessage);
        
        if(jsonMessage instanceof TextMessage) {
        	
        	//OrderInProgressEvent event = null;
        	OrderSubmitedEvent orderEvent = null;
        	
        	TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
           
            try {
            	orderEvent = mapper.readValue(messageData, OrderSubmitedEvent.class);
                
                //TOTO optionnal
                Order order = new Order();
                order.setExternalCartId(orderEvent.getCartId());
                order.setTotalAmount(orderEvent.getTotalCartAmount());
                orderRepository.save(order);
                
             
                
            } catch (Exception e) {
                logger.log(Level.SEVERE,"error converting to cart", e);
            }
            System.out.println("messageData:"+messageData);
        }
       
    }
}
