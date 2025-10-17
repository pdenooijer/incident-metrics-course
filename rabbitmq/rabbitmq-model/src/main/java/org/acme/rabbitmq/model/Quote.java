package org.acme.rabbitmq.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Quote {

    public String id;
    public int price;
    public int duration;

    /**
    * Default constructor required for Jackson serializer
    */
    public Quote() { }

    public Quote(String id, int price, int duration) {
        this.id = id;
        this.price = price;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                '}';
    }
}
