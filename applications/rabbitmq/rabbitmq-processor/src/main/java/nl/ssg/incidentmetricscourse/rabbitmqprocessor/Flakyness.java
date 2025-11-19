package nl.ssg.incidentmetricscourse.rabbitmqprocessor;

import java.util.Random;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class Flakyness<T> {
    private Random random = new Random();

    private final float successRate;
    private Supplier<T> success;
    private Supplier<T> error;

    public static <T> Flakyness<T> withRate(float successRate) {
        return new Flakyness(successRate);
    }

    public Flakyness<T> eitherGet(Supplier<T> success) {
        this.success = success;
        return this;
    }

    public T or(Supplier<T> error) {
        this.error = error;
        return doGet();
    }


    @SneakyThrows
    private T doGet(){
        boolean willSucceed = random.nextFloat() < successRate;
        if (willSucceed) {
            return success.get();
        } else {
            return error.get();
        }
    }

}
