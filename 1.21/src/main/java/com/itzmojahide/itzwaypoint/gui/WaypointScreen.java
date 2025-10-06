package com.itzmojahide.itzwaypoint.gui;

import com.itzmojahide.itzwaypoint.manager.WaypointManager;
import com.itzmojahide.itzwaypoint.waypoint.Waypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class WaypointScreen extends Screen {

    private enum Tab { WAYPOINTS, DEATHPOINTS }
    private Tab currentTab = Tab.WAYPOINTS;
    private List<Waypoint> currentList;
    private final Screen parent;

    // For creating/editing waypoints
    private TextFieldWidget nameField, xField, yField, zField, colorField;
    private boolean isCreatingOrEditing = false;
    private Waypoint waypointToEdit = null;

    public WaypointScreen(Screen parent) {
        super(Text.literal("ItzWaypoint"));
        this.parent = parent;
        refreshLists();
    }

    private void refreshLists() {
        if (currentTab == Tab.WAYPOINTS) {
            this.currentList = new ArrayList<>(WaypointManager.waypoints.values());
        } else {
            this.currentList = new ArrayList<>(WaypointManager.deathpoints.values());
        }
    }

    @Override
    protected void init() {
        super.init();
        if (isCreatingOrEditing) {
            initCreationMenu();
        } else {
            initMainMenu();
        }
    }
    
    private void initMainMenu() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Waypoints"), b -> switchTab(Tab.WAYPOINTS)).dimensions(this.width / 2 - 105, 30, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Death Points"), b -> switchTab(Tab.DEATHPOINTS)).dimensions(this.width / 2 + 5, 30, 100, 20).build());

        if (currentTab == Tab.WAYPOINTS) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Create New Waypoint"), b -> {
                this.isCreatingOrEditing = true;
                this.waypointToEdit = null; // Ensure we are creating, not editing
                this.clearChildren();
                init();
            }).dimensions(this.width / 2 - 100, 55, 200, 20).build());
        }

        int yPos = 85;
        for (Waypoint wp : this.currentList) {
            this.addDrawableChild(new TextFieldWidget(textRenderer, this.width / 2 - 100, yPos, 140, 20, Text.literal(wp.name))).setText(wp.name);
            
            this.addDrawableChild(ButtonWidget.builder(Text.literal("X"), b -> {
                if (currentTab == Tab.WAYPOINTS) WaypointManager.deleteWaypoint(wp.name);
                else WaypointManager.deleteDeathPoint(wp.name);
                refreshAndRebuild();
            }).dimensions(this.width / 2 + 50, yPos, 20, 20).build());

            if (currentTab == Tab.WAYPOINTS) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("E"), b -> {
                    this.isCreatingOrEditing = true;
                    this.waypointToEdit = wp;
                    this.clearChildren();
                    init();
                }).dimensions(this.width / 2 + 75, yPos, 20, 20).build());
            }
            yPos += 25;
        }
    }

    private void initCreationMenu() {
        Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
        String title = waypointToEdit == null ? "Creating new Waypoint" : "Editing Waypoint";
        
        nameField = new TextFieldWidget(textRenderer, this.width / 2 - 100, 60, 200, 20, Text.literal("Enter Waypoint Name"));
        xField = new TextFieldWidget(textRenderer, this.width / 2 - 100, 85, 60, 20, Text.literal("X"));
        yField = new TextFieldWidget(textRenderer, this.width / 2 - 35, 85, 60, 20, Text.literal("Y"));
        zField = new TextFieldWidget(textRenderer, this.width / 2 + 30, 85, 70, 20, Text.literal("Z"));
        colorField = new TextFieldWidget(textRenderer, this.width / 2 - 100, 110, 200, 20, Text.literal("Color Hex (e.g., FFFFFF)"));
        
        if (waypointToEdit != null) {
            nameField.setText(waypointToEdit.name);
            xField.setText(String.valueOf((int)waypointToEdit.x));
            yField.setText(String.valueOf((int)waypointToEdit.y));
            zField.setText(String.valueOf((int)waypointToEdit.z));
            colorField.setText(Integer.toHexString(waypointToEdit.color).substring(2).toUpperCase());
        } else {
             xField.setText(String.valueOf((int)playerPos.x));
             yField.setText(String.valueOf((int)playerPos.y));
             zField.setText(String.valueOf((int)playerPos.z));
        }

        this.addDrawableChild(nameField);
        this.addDrawableChild(xField);
        this.addDrawableChild(yField);
        this.addDrawableChild(zField);
        this.addDrawableChild(colorField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save Waypoint"), b -> saveWaypoint()).dimensions(this.width / 2 - 100, 140, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> {
            isCreatingOrEditing = false;
            refreshAndRebuild();
        }).dimensions(this.width / 2 - 100, 165, 200, 20).build());
    }

    private void saveWaypoint() {
        try {
            String name = nameField.getText();
            if (name.isEmpty()) return;
            
            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            double z = Double.parseDouble(zField.getText());
            int color = 0xFF000000 | Integer.parseInt(colorField.getText(), 16);

            // If we were editing, delete the old one first
            if (waypointToEdit != null && !waypointToEdit.name.equals(name)) {
                WaypointManager.deleteWaypoint(waypointToEdit.name);
            }

            WaypointManager.saveOrUpdateWaypoint(new Waypoint(name, x, y, z, color));
            isCreatingOrEditing = false;
            refreshAndRebuild();
        } catch (NumberFormatException e) {
            // Handle error (e.g., show a message on screen)
        }
    }

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        refreshAndRebuild();
    }

    private void refreshAndRebuild() {
        refreshLists();
        this.clearChildren();
        this.init();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        String title = isCreatingOrEditing ? (waypointToEdit == null ? "Creating new Waypoint" : "Editing Waypoint") : "ItzWaypoint";
        context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 10, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
