package com.example.restaurant.config;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderEvents;
import com.example.restaurant.model.OrderStatusValues;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatusValues, OrderEvents> {


    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusValues, OrderEvents> states) throws Exception {
        states.withStates()
                .initial(OrderStatusValues.PLACED)
                .end(OrderStatusValues.CANCELED)
                .end(OrderStatusValues.RECEIVED)
                .states(EnumSet.allOf(OrderStatusValues.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusValues, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatusValues.PLACED).target(OrderStatusValues.PROCESSING).event(OrderEvents.PREPARE)
                .and()
                .withExternal().source(OrderStatusValues.PLACED).target(OrderStatusValues.CANCELED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStatusValues.PROCESSING).target(OrderStatusValues.IN_ROUTE).event(OrderEvents.DISPATCH)
                .and()
                .withExternal().source(OrderStatusValues.IN_ROUTE).target(OrderStatusValues.DELIVERED).event(OrderEvents.DELIVER)
                .and()
                .withExternal().source(OrderStatusValues.DELIVERED).target(OrderStatusValues.RECEIVED).event(OrderEvents.RECEIVE);
    }
}
