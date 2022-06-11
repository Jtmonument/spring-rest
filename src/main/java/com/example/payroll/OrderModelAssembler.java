package com.example.payroll;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
    @Override
    public EntityModel<Order> toModel(Order order) {
        EntityModel<Order> entity = EntityModel.of(order,
                linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders"));
        if (order.getStatus() == Status.IN_PROGRESS) {
            entity.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            entity.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }
        return entity;
    }
}
