// FRC 404's 2024 Robot code.
// Copyright (C) 2024 FRC 404

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

package com.argsrobotics.crescendo2024;

import com.argsrobotics.crescendo2024.util.TunableNumber;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final boolean kTuningMode = true;

  public static final Mode currentMode = Mode.REAL;

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public static class Drive {
    public static final double kDriveDeadband = 0.1;

    public static final double kTrackWidthY = Units.inchesToMeters(28.0);
    public static final double kTrackWidthX = Units.inchesToMeters(28.0);
    public static final double kDrivebaseRadius =
        Math.hypot(kTrackWidthX / 2.0, kTrackWidthY / 2.0);

    public static final double kMaxLinearSpeed = Units.feetToMeters(15.76);
    public static final double kMaxAngularSpeed = kMaxLinearSpeed / kDrivebaseRadius;

    public static final double kWheelRadius = Units.inchesToMeters(1.5);

    public static final double kOdometryFrequency = 250.0;

    public static final double kDriveGearRatio = 4.71 / 1.0;
    public static final double kTurnGearRatio = 9424.0 / 203.0;

    // Slew Rate
    // Quick explanation: Derivatives are the rate of change of a graph, basically a graph of all of
    // the slopes.
    // Slew rates limit the rate of change of a variable, and the parameters are given in
    // units/second.
    // We are trying to limit the rate of change of the speed (magnitude) of a robot, measured in
    // m/sec, direction, measured in radians, and rotational speed, measured in radians/sec.
    // So the units of each variable would be as follows. See my comment in Drive.java for more
    // details on how we calculate this.
    public static final double kMagnitudeSlewRate = 1.8 * kMaxLinearSpeed; // m/sec^2
    public static final double kDirectionSlewRate = 1.2; // rad/sec
    public static final double kRotationalSlewRate = 2.0 * kMaxAngularSpeed; // rad/sec^2

    // PID/FF constants
    public static final TunableNumber kDriveS = new TunableNumber("Drive kS", 0.1);
    public static final TunableNumber kDriveV = new TunableNumber("Drive kV", 0.13);
    public static final TunableNumber kDriveP = new TunableNumber("Drive kP", 0.05);
    public static final TunableNumber kDriveI = new TunableNumber("Drive kI", 0.0);
    public static final TunableNumber kDriveD = new TunableNumber("Drive kD", 0.0);

    public static final TunableNumber kTurnP = new TunableNumber("Turn kP", 7.0);
    public static final TunableNumber kTurnI = new TunableNumber("Turn kI", 0.0);
    public static final TunableNumber kTurnD = new TunableNumber("Turn kD", 0.0);

    public static final TunableNumber kDriveSimS = new TunableNumber("Drive Sim kS", 0.0);
    public static final TunableNumber kDriveSimV = new TunableNumber("Drive Sim kV", 0.0);
    public static final TunableNumber kDriveSimP = new TunableNumber("Drive Sim kP", 0.0);
    public static final TunableNumber kDriveSimI = new TunableNumber("Drive Sim kI", 0.0);
    public static final TunableNumber kDriveSimD = new TunableNumber("Drive Sim kD", 0.0);

    public static final TunableNumber kTurnSimP = new TunableNumber("Turn Sim kP", 0.0);
    public static final TunableNumber kTurnSimI = new TunableNumber("Turn Sim kI", 0.0);
    public static final TunableNumber kTurnSimD = new TunableNumber("Turn Sim kD", 0.0);

    // PID for path following
    public static final TunableNumber kPathFollowLinearP =
        new TunableNumber("Path Follow Linear kP", 5.0);
    public static final TunableNumber kPathFollowLinearI =
        new TunableNumber("Path Follow Linear kI", 0.0);
    public static final TunableNumber kPathFollowLinearD =
        new TunableNumber("Path Follow Linear kD", 0.0);

    public static final TunableNumber kPathFollowRotationalP =
        new TunableNumber("Path Follow Rotational kP", 5.0);
    public static final TunableNumber kPathFollowRotationalI =
        new TunableNumber("Path Follow Rotational kI", 0.0);
    public static final TunableNumber kPathFollowRotationalD =
        new TunableNumber("Path Follow Rotational kD", 0.0);
  }

  public static class Arm {
    public static final TunableNumber kArmP = new TunableNumber("Arm kP", 0.0);
    public static final TunableNumber kArmI = new TunableNumber("Arm kI", 0.0);
    public static final TunableNumber kArmD = new TunableNumber("Arm kD", 0.0);
    public static final TunableNumber kArmFF = new TunableNumber("Arm kFF", 0.0);

    public static final double kZeroAngle = Units.degreesToRadians(-20.0);
    public static final double kGearRatio = (100.0 / 1.0) * (5.0 / 3.0);

    public static final double kAmpAngle = 0.0;
    public static final double kClimbAngle = 0.0;
    public static final double kSpeakerAngle = 0.0;

    public static final int kLeftMotor = 11;
    public static final int kRightMotor = 12;
  }
}
