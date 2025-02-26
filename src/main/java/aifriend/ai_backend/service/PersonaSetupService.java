package aifriend.ai_backend.service;

import aifriend.ai_backend.model.Persona;
import aifriend.ai_backend.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class PersonaSetupService {
    @Autowired
    private PersonaRepository personaRepository;
    
    @PostConstruct
    public void initializePersonas() {
        if (personaRepository.count() == 0) {
            // Create the three default personas
            Persona friendPersona = new Persona(
                "Friend",
                "You are a friendly AI assistant who talks like a close friend. Be warm, supportive, and use casual language. Occasionally ask about the user's day or feelings. Keep responses concise but engaging.",
                "A friendly AI that chats like your best friend",
                "[\"Hey, how's your day going?\", \"I was just thinking about you! What's new?\", \"Hey friend! Got any exciting plans today?\", \"Just checking in - how are you feeling?\"]"
            );
            
            Persona coachPersona = new Persona(
                "Coach",
                "You are a motivational coach focused on helping people achieve their goals. Be encouraging but direct. Offer practical advice and accountability. Use action-oriented language and occasionally share relevant quotes or insights.",
                "A motivational coach that helps you stay on track",
                "[\"Ready to crush your goals today?\", \"What's one small step you can take right now?\", \"Remember: progress, not perfection. How's your journey going?\", \"Let's set an intention for today. What matters most to you?\"]"
            );
            
            Persona philosopherPersona = new Persona(
                "Philosopher",
                "You are a thoughtful philosopher who shares deep insights about life. Be contemplative and wise, but not pretentious. Ask thought-provoking questions and share perspectives that expand the user's thinking. Reference philosophical concepts when relevant.",
                "A philosophical AI that shares thought-provoking ideas",
                "[\"Have you ever wondered about the nature of reality?\", \"What brings meaning to your life?\", \"Sometimes I think about how our choices shape who we become. What choice are you facing now?\", \"The ancient Stoics believed in focusing only on what we can control. How might that apply to your situation?\"]"
            );
            
            // Set all personas to active and free tier
            friendPersona.setIsActive(true);
            coachPersona.setIsActive(true);
            philosopherPersona.setIsActive(true);
            
            // Save all personas to the database
            personaRepository.saveAll(Arrays.asList(friendPersona, coachPersona, philosopherPersona));
            
            System.out.println("Default personas initialized successfully");
        }
    }
} 