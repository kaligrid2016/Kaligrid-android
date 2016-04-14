package com.kaligrid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomEventRecipientGenerator {

    public static List<String> generate() {
        List<String> recipients = new ArrayList<>();
        recipients.add("Daniel");
        recipients.add("Seula");
        recipients.add("Brad");
        recipients.add("Xingy");

        Collections.shuffle(recipients);

        Random random = new Random();
        int max = random.nextInt(4) + 1;

        return recipients.subList(0, max);
    }
}
