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

package com.argsrobotics.crescendo2024.subsystems.drive;

import static com.argsrobotics.crescendo2024.Constants.kTuningMode;
import static com.argsrobotics.crescendo2024.Constants.Drive.*;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkLowLevel.PeriodicFrame;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.SparkAbsoluteEncoder.Type;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import java.util.Queue;

/**
 * Module IO implementation for SparkMax drive motor controller, SparkMax turn motor controller (NEO
 * 550), and Rev through bore absolute encoders based off of AdvantageKit template
 * (https://github.com/Mechanical-Advantage/AdvantageKit).
 */
public class ModuleIOSparkMax implements ModuleIO {
  private final CANSparkMax driveSparkMax;
  private final CANSparkMax turnSparkMax;

  private SimpleMotorFeedforward driveFeedforward;
  private final SparkPIDController drivePidController;
  private final SparkPIDController turnPidController;

  private final RelativeEncoder driveEncoder;
  private final AbsoluteEncoder turnEncoder;
  private final Queue<Double> drivePositionQueue;
  private final Queue<Double> turnPositionQueue;

  private final boolean isTurnEncoderInverted = false;
  private double chassisAngularOffset = 0.0;

  public ModuleIOSparkMax(int index, double chassisAngularOffset) {
    this.chassisAngularOffset = chassisAngularOffset;
    switch (index) {
      case 0:
        driveSparkMax = new CANSparkMax(1, MotorType.kBrushless);
        turnSparkMax = new CANSparkMax(2, MotorType.kBrushless);
        break;
      case 1:
        driveSparkMax = new CANSparkMax(3, MotorType.kBrushless);
        turnSparkMax = new CANSparkMax(4, MotorType.kBrushless);
        break;
      case 2:
        driveSparkMax = new CANSparkMax(5, MotorType.kBrushless);
        turnSparkMax = new CANSparkMax(6, MotorType.kBrushless);
        break;
      case 3:
        driveSparkMax = new CANSparkMax(7, MotorType.kBrushless);
        turnSparkMax = new CANSparkMax(8, MotorType.kBrushless);
        break;
      default:
        throw new RuntimeException("Invalid module index");
    }

    driveSparkMax.restoreFactoryDefaults();
    turnSparkMax.restoreFactoryDefaults();

    driveSparkMax.setCANTimeout(250);
    turnSparkMax.setCANTimeout(250);

    driveEncoder = driveSparkMax.getEncoder();
    turnEncoder = turnSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    drivePidController = driveSparkMax.getPIDController();
    turnPidController = turnSparkMax.getPIDController();

    driveSparkMax.setSmartCurrentLimit(40);
    turnSparkMax.setSmartCurrentLimit(20);
    driveSparkMax.enableVoltageCompensation(12.0);
    turnSparkMax.enableVoltageCompensation(12.0);

    driveEncoder.setPosition(0.0);
    driveEncoder.setMeasurementPeriod(10);
    driveEncoder.setAverageDepth(2);
    turnEncoder.setAverageDepth(2);
    turnEncoder.setInverted(isTurnEncoderInverted);

    turnPidController.setPositionPIDWrappingEnabled(true);
    turnPidController.setPositionPIDWrappingMinInput(0);
    turnPidController.setPositionPIDWrappingMaxInput(2 * Math.PI);

    driveEncoder.setPositionConversionFactor((kWheelRadius * 2 * Math.PI) / kDriveGearRatio);
    driveEncoder.setVelocityConversionFactor(((2 * Math.PI * kWheelRadius) / kDriveGearRatio) / 60.0);

    turnEncoder.setPositionConversionFactor(2 * Math.PI);
    turnEncoder.setVelocityConversionFactor((2 * Math.PI) / 60.0);

    drivePidController.setFeedbackDevice(driveEncoder);
    turnPidController.setFeedbackDevice(turnEncoder);

    driveFeedforward = new SimpleMotorFeedforward(kDriveS.get(), kDriveV.get());
    drivePidController.setP(kDriveP.get());
    drivePidController.setI(kDriveI.get());
    drivePidController.setD(kDriveD.get());
    drivePidController.setFF(0);

    turnPidController.setP(kTurnP.get());
    turnPidController.setI(kTurnI.get());
    turnPidController.setD(kTurnD.get());
    turnPidController.setFF(kTurnFF.get());


    driveSparkMax.setCANTimeout(0);
    turnSparkMax.setCANTimeout(0);

    driveSparkMax.setPeriodicFramePeriod(
        PeriodicFrame.kStatus2, (int) (1000.0 / kOdometryFrequency));
    turnSparkMax.setPeriodicFramePeriod(
        PeriodicFrame.kStatus2, (int) (1000.0 / kOdometryFrequency));

    drivePositionQueue =
        SparkMaxOdometryThread.getInstance().registerSignal(driveEncoder::getPosition);
    turnPositionQueue =
        SparkMaxOdometryThread.getInstance().registerSignal(turnEncoder::getPosition);

    driveSparkMax.burnFlash();
    turnSparkMax.burnFlash();
  }

  @Override
  public void updateInputs(ModuleIOInputs inputs) {
    if (kTuningMode) {
      drivePidController.setP(kDriveP.get());
      drivePidController.setI(kDriveI.get());
      drivePidController.setD(kDriveD.get());
      drivePidController.setFF(0);

      turnPidController.setP(kTurnP.get());
      turnPidController.setI(kTurnI.get());
      turnPidController.setD(kTurnD.get());
      turnPidController.setFF(kTurnFF.get());

      driveFeedforward = new SimpleMotorFeedforward(kDriveS.get(), kDriveV.get());
    }

    inputs.drivePositionMeters = driveEncoder.getPosition();
    inputs.driveVelocityMetersPerSec = driveEncoder.getVelocity();
    inputs.driveAppliedVolts = driveSparkMax.getAppliedOutput() * driveSparkMax.getBusVoltage();
    inputs.driveCurrentAmps = new double[] {driveSparkMax.getOutputCurrent()};

    inputs.turnAbsolutePosition =
        Rotation2d.fromRadians(turnEncoder.getPosition() - chassisAngularOffset);
    inputs.turnPosition =
        Rotation2d.fromRadians(turnEncoder.getPosition() - chassisAngularOffset);
    inputs.turnVelocityRadPerSec =
        Units.rotationsPerMinuteToRadiansPerSecond(turnEncoder.getVelocity());
    inputs.turnAppliedVolts = turnSparkMax.getAppliedOutput() * turnSparkMax.getBusVoltage();
    inputs.turnCurrentAmps = new double[] {turnSparkMax.getOutputCurrent()};

    inputs.odometryDrivePositionsRad =
        drivePositionQueue.stream()
            .mapToDouble((Double value) -> Units.rotationsToRadians(value) / kDriveGearRatio)
            .toArray();
    inputs.odometryTurnPositions =
        turnPositionQueue.stream()
            .map(
                (Double value) ->
                    Rotation2d.fromRotations(value)
                        .minus(Rotation2d.fromRadians(chassisAngularOffset)))
            .toArray(Rotation2d[]::new);
    drivePositionQueue.clear();
    turnPositionQueue.clear();
  }

  @Override
  public double getAngularOffset() {
    return chassisAngularOffset;
  }

  @Override
  public void setDriveVoltage(double volts) {
    driveSparkMax.setVoltage(volts);
  }

  @Override
  public void setTurnVoltage(double volts) {
    turnSparkMax.setVoltage(volts);
  }

  public void setDriveVelocity(double velocity) {
    drivePidController.setReference(velocity, ControlType.kVelocity, 0, driveFeedforward.calculate(velocity));
  }

  public void setTurnAngle(Rotation2d angle) {
    turnPidController.setReference(angle.getRadians(), ControlType.kPosition);
  }

  @Override
  public void setDriveBrakeMode(boolean enable) {
    driveSparkMax.setIdleMode(enable ? IdleMode.kBrake : IdleMode.kCoast);
  }

  @Override
  public void setTurnBrakeMode(boolean enable) {
    turnSparkMax.setIdleMode(enable ? IdleMode.kBrake : IdleMode.kCoast);
  }

  @Override
  public void close() {
    driveSparkMax.close();
    turnSparkMax.close();
  }
}
