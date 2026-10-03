package com.example.hub.chatbot.service;

import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NativeChatbotService {

    private final Map<String, String> memory = new ConcurrentHashMap<>();

    public String reply(String sessionId, String message) {
        String msg = message.toLowerCase(Locale.ROOT).trim();

        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey"))
            return "Hello! How can I help you today?";
        if (msg.contains("your name"))
            return "I'm NativeBot, a simple rule-based assistant.";
        if (msg.contains("time"))
            return "Current server time: " + java.time.LocalTime.now();
        if (msg.contains("date"))
            return "Today's date: " + java.time.LocalDate.now();
        if (msg.contains("help"))
            return "Try: hi, your name, time, date, weather, joke, bye";
        if (msg.contains("weather"))
            return "I can't fetch live weather without an API key, but it's always sunny in code!";
        if (msg.contains("joke"))
            return "Why do Java devs wear glasses? Because they don't C#.";
        if (msg.startsWith("my name is "))
        {
            String name = message.substring(11).trim();
            memory.put(sessionId, name);
            return "Nice to meet you, " + name + "!";
        }
        if (msg.contains("what is my name") || msg.contains("who am i"))
            return memory.getOrDefault(sessionId, "I don't know your name yet.");
        if (msg.contains("bye"))
            return "Goodbye! Have a great day.";

        return "I don't understand that yet. Type 'help' to see what I can do.";
    }
}