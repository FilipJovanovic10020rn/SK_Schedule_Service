package com.example.scheduleservice.components;

import com.example.scheduleservice.messagebroker.MessageSender;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.requests.NotifyServiceBookDTO;
import com.example.scheduleservice.service.WorkoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutCancellationScheduler {

    private final WorkoutService workoutService;

    private final MessageSender messageSender;

    public WorkoutCancellationScheduler(WorkoutService workoutService, MessageSender messageSender) {
        this.workoutService = workoutService;
        this.messageSender = messageSender;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkWorkoutsForCancellation() throws JsonProcessingException {
        List<Workout> upcomingWorkouts = workoutService.findUpcomingWorkouts();
        for (Workout workout : upcomingWorkouts) {
            if (workout.isCancellationRequired()) {
                workoutService.delete(workout);

            }
            else{
                List<Long> clients = workoutService.findAllClientsByWorkoutID(workout.getId());
                for(Long client: clients){
                    NotifyServiceBookDTO notifyServiceBookDTO = new NotifyServiceBookDTO(client,workout.getRoom().getManagerId(),
                            workout.getName(),workout.getDate());
                    messageSender.sendMessage("notify-service/reminder",notifyServiceBookDTO );
                }

            }
        }
    }
}