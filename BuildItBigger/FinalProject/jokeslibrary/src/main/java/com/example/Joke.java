package com.example;

public class Joke {

    private static String[] jokes = {
            "Q: Why did the cat go to Minnesota?\n" + "A: To get a mini soda!",
            "Q: Where do orcas hear music?\n" + "A: Orca-stras!",
            "Q: Why did the cow cross the road?\n" + "A: To get to the udder side.",
            "Q: What do you call a fish without an eye?\n" + "A: Fsh!",
            "Q: What do you do if your dog chews a dictionary?\n" + "A: Take the words out of his mouth!"
    };

    public static String tellJokes() {
        int i = (int) ((Math.random() * 5) % 5);
        return jokes[i];
    }
}
