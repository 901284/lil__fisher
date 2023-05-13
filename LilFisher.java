import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import java.awt.*;
import org.osbot.rs07.api.ui.Tab;

@ScriptManifest(author = "Lil Ugly Mane", name = "LilFisher", info = "I fish!", version = 0.1, logo = "")
public final class LilFisher extends Script {

    private long startTime;
    private final Font font = new Font("Helvetica", Font.PLAIN, 12);
    public final String rod = "Barbarian rod";
    public final String action = "Use-rod";

    @Override
    public void onStart() {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.FISHING);
        log("Lets go!");
    }

    public boolean canFish() {
        return (getInventory().contains(rod, "Feather") && (!getInventory().isFull()));
    }

    public boolean cantFish() {
        return (!getInventory().contains(rod, "Feather"));
    }

    public void fish() throws InterruptedException {
        NPC spot = getNpcs().closest("Fishing spot");

        if ((!myPlayer().isAnimating()) && spot != null) {
            spot.interact(action);
            new ConditionalSleep(10000, 1000) {
                @Override
                public boolean condition() {
                    return myPlayer().isAnimating();
                }
            }.sleep();
            if (random(1, 100) == 50 ) {
                logger.debug("Checking experience randomly");
                tabs.open(Tab.SKILLS);
                RS2Widget fishingWidget = widgets.get(320, Skill.FISHING.getChildId());
                if (fishingWidget != null) {
                    fishingWidget.hover();
                }
                sleep(random(1000, 5000));
                tabs.open(Tab.INVENTORY);
            }
            if (random(1, 10) <= 9) {
                getMouse().moveOutsideScreen();
            }
        }
    }

    private void drop() throws InterruptedException {
        if (getInventory().isFull()) {
            sleep(random(500, 4000));
            getInventory().dropAllExcept(rod, "Feather");
            new ConditionalSleep(5000, 600, 500) {
                @Override
                public boolean condition() {
                    return (getInventory().isEmptyExcept(rod, "Feather"));
                }
            }.sleep();
        }
    }

    @Override
    public int onLoop() throws InterruptedException {
        if (canFish()) {
            fish();
        } else if (cantFish()) {
            stop();
        }
        else {
            drop();
        }
        return random(1000, 2000);
    }

    @Override
    public void onExit() {
        log("You have gained " + getExperienceTracker().getGainedXP(Skill.FISHING) + " experience!");
    }

    @Override
    public void onPaint(Graphics2D g) {
        // Set the font
        g.setFont(font);
        g.setColor(Color.WHITE);

        // Draw paint info
        g.drawString("LilFisher by Lil Ugly Mane", 10, 265);
        g.drawString("Runtime: " + formatTime(System.currentTimeMillis() - startTime), 10, 285);
        g.drawString("Fishing XP (p/h): " + getExperienceTracker().getGainedXP(Skill.FISHING) + " (" +
                getExperienceTracker().getGainedXPPerHour(Skill.FISHING) + ")", 10, 300);
        g.drawString("Fishing Level: " + getSkills().getStatic(Skill.FISHING) + " (" +
                getExperienceTracker().getGainedLevels(Skill.FISHING) + ")", 10, 315);
        g.drawString("Time to level: " + formatTime(getExperienceTracker().getTimeToLevel(Skill.FISHING)), 10, 330);
    }

    private String formatTime(final long time) {
        long s = time / 1000, m = s / 60, h = m / 60;
        s %= 60;
        m %= 60;
        h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}