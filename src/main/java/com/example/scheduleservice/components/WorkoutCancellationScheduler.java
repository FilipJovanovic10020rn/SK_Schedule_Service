package com.example.scheduleservice.components;

import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.service.WorkoutService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutCancellationScheduler {

    private final WorkoutService workoutService;

    public WorkoutCancellationScheduler(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkWorkoutsForCancellation() {
        List<Workout> upcomingWorkouts = workoutService.findUpcomingWorkouts();
        for (Workout workout : upcomingWorkouts) {
            if (workout.isCancellationRequired()) {
                workoutService.delete(workout);
            }
        }
    }
}