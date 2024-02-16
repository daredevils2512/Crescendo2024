// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveSub;
import frc.robot.subsystems.IntakeSub;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class DriveCommands extends Command {

public static Command toggleInverted(DriveSub driveSub){
  // return driveSub.runOnce(()-> driveSub.setInverted(!driveSub.getInverted()));
  return new InstantCommand(() -> driveSub.setInverted(false), driveSub);
}

}
