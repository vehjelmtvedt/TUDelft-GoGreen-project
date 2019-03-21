package tools;

import data.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ActivityQueriesTest {
    User activeUser = new User("Active", "User", 20, "active_user@email.com", "active_user", "pwd123");
    ActivityQueries activityQuery = new ActivityQueries(activeUser.getActivities());
    Date today = Calendar.getInstance().getTime();


    @Test
    public void testConstructor() {
        ActivityQueries activityQueries = new ActivityQueries(activeUser.getActivities());

        Assert.assertEquals(activeUser.getActivities(), activityQueries.getActivities());
    }

    @Test
    public void testGetter() {
        Assert.assertEquals(activeUser.getActivities(), activityQuery.getActivities());
    }

    @Test
    public void testSetter() {
        ActivityQueries activityQueries = new ActivityQueries(activeUser.getActivities());
        activityQueries.setActivities(null);

        Assert.assertNull(activityQueries.getActivities());
    }

    @Test
    public void testFilterByCategory() {
        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(new EatVegetarianMeal());
        activities.add(new EatVegetarianMeal());
        activities.add(new BuyLocallyProducedFood());
        activities.add(new BuyNonProcessedFood());
        activities.add(new EatVegetarianMeal());

        for (Activity a : activities)
            activeUser.addActivity(a);

        List<Activity> filteredActivities = activityQuery.filterActivities("Food");

        Assert.assertEquals(activities, filteredActivities);
    }

    @Test
    public void testTotalCO2Saved() {
        double sum = 0.0;

        for (int i = 0; i < 10; ++i) {
            Activity activity = new EatVegetarianMeal();
            double CO2Saved = (i+1)*(i+1);
            activity.setCarbonSaved(CO2Saved);

            sum += CO2Saved;
            activeUser.addActivity(activity);
        }

        Assert.assertEquals(sum, activityQuery.getTotalCO2Saved(), 0.1);
    }

    private void addActivitiesToUser(User user, int diffFromToday, int entries, int dateDiff) {
        // Rewind Calendar by difference
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -diffFromToday);

        for (int i = 0; i < entries; ++i) {
            Activity activity = new EatVegetarianMeal();
            activity.setDate(calendar.getTime());
            activity.setCarbonSaved((i+1)*15);

            user.addActivity(activity);

            calendar.add(Calendar.DATE, dateDiff);
        }
    }

    private ArrayList<Activity> getExpectedDateFilteredList(List<Activity> activityList, Date fromDate, Date toDate) {
        return activityList.stream()
                .filter((activity -> activity.getDate().equals(fromDate)
                        || activity.getDate().equals(toDate)
                        || (activity.getDate().before(toDate)
                        && activity.getDate().after(fromDate))))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private double filteredByDateSum(List<Activity> activityList) {
        return activityList.stream()
                .mapToDouble(Activity::getCarbonSaved)
                .sum();
    }

    private Date getDateRewind(int diff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -diff);
        return calendar.getTime();
    }

    @Test
    public void testFilterByDate() {
        Date fromDate = getDateRewind(20);

        addActivitiesToUser(activeUser, 40, 100, 1);
        ArrayList<Activity> expected = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        Assert.assertEquals(expected, activityQuery.filterActivitiesByDate(fromDate, today));
    }

    @Test
    public void testFilterByDateNone() {
        Date fromDate = getDateRewind(20);

        addActivitiesToUser(activeUser, 200, 100, 1);
        ArrayList<Activity> expected = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        Assert.assertEquals(expected, activityQuery.filterActivitiesByDate(fromDate, today));
    }

    @Test
    public void testFilterByDateOne() {
        addActivitiesToUser(activeUser, 0, 5, 1);
        Assert.assertEquals(1, activityQuery.filterActivitiesByDate(today, today).size());
    }

    @Test
    public void testFilterByDateAllInRage() {
        Date fromDate = getDateRewind(10);

        addActivitiesToUser(activeUser, 7, 5, 1);
        ArrayList<Activity> expected = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        Assert.assertEquals(expected, activityQuery.filterActivitiesByDate(fromDate, today));
    }

    @Test
    public void testFilterByDateNoActivities() {
        ArrayList<Activity> expected = new ArrayList<>();

        Assert.assertEquals(expected, activityQuery.filterActivitiesByDate(today, today));
    }

    @Test
    public void testGetTotalCO2SavedOverWeek() {
        Date fromDate = getDateRewind(DateUnit.WEEK.getNumDays());

        addActivitiesToUser(activeUser, 14, 21, 1);
        ArrayList<Activity> activityList = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        double expected = filteredByDateSum(activityList);

        Assert.assertEquals(expected, activityQuery.getTotalCO2Saved(DateUnit.WEEK), 0.1);
    }

    @Test
    public void testGetTotalCO2SavedOverMonth() {
        Date fromDate = getDateRewind(DateUnit.MONTH.getNumDays());

        addActivitiesToUser(activeUser, 125, 500, 2);
        ArrayList<Activity> activityList = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        double expected = filteredByDateSum(activityList);

        Assert.assertEquals(expected, activityQuery.getTotalCO2Saved(DateUnit.MONTH), 0.1);
    }

    @Test
    public void testGetTotalCO2SavedOverPeriod() {
        Date fromDate = getDateRewind(DateUnit.MONTH.getNumDays()*3);

        addActivitiesToUser(activeUser, 145, 600, 1);
        ArrayList<Activity> activityList = getExpectedDateFilteredList(activeUser.getActivities(), fromDate, today);

        double expected = filteredByDateSum(activityList);

        Assert.assertEquals(expected, activityQuery.getTotalCO2Saved(fromDate), 0.1);
    }

    @Test
    public void testFilterDuplicates() {
        addActivitiesToUser(activeUser, 0, 243, 0);
        ArrayList<Activity> expected = getExpectedDateFilteredList(activeUser.getActivities(), today, today);

        Assert.assertEquals(expected, activityQuery.filterActivitiesByDate(today, today));
    }
}