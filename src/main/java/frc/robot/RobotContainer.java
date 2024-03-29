package frc.robot;

import frc.robot.Constants.IoConstants;
import frc.robot.commands.AutoCommands;
import frc.robot.commands.ClimberCommands;
import frc.robot.commands.DriveCommands;
import frc.robot.commands.IntakeCommands;
import frc.robot.commands.ShooterCommands;
import frc.robot.io.Extreme;
import frc.robot.subsystems.ClimberSub;
import frc.robot.subsystems.DriveSub;
import frc.robot.subsystems.IntakeSub;
import frc.robot.subsystems.ShooterArmSub;
import frc.robot.subsystems.ShooterSub;
import frc.robot.subsystems.ShooterArmSub.Position;

import java.util.function.DoubleSupplier;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class RobotContainer {
  private final DriveSub driveSub = new DriveSub();
  private final IntakeSub intakeSub = new IntakeSub();
  private final ShooterSub shooterSub = new ShooterSub();
  private final ShooterArmSub shooterArmSub = new ShooterArmSub();
  private final ClimberSub climberSub = new ClimberSub();
  private final Extreme extreme = new Extreme(1);
  private final CommandXboxController xbox = new CommandXboxController(IoConstants.XBOX_CONTROLLER_PORT);
  private enum Auto {
    None, DriveForward, BlueFullAuto, RedFullAuto, SpeakerAuto, StageAuto
  }
  private final SendableChooser<Auto> chooser = new SendableChooser<>();

  public RobotContainer() {
    configureBindings();

    chooser.setDefaultOption("Move Forward", Auto.DriveForward);
    chooser.addOption("None", Auto.None);
    chooser.addOption("Blue Full Auto", Auto.BlueFullAuto);
    chooser.addOption("Red Full Auto", Auto.RedFullAuto);
    chooser.addOption("Speaker Auto", Auto.SpeakerAuto);
    chooser.addOption("Stage Auto", Auto.StageAuto);
    SmartDashboard.putData("Auto", chooser);

    // UsbCamera intakeCamera = CameraServer.startAutomaticCapture();
    // intakeCamera.setResolution(640, 480);
    // CameraServer.getServer();
    
  }

  private void configureBindings() {

    // Drivetrain
    DoubleSupplier move = () -> MathUtil.applyDeadband(-xbox.getLeftY(), 0.1);
    DoubleSupplier turn = () -> MathUtil.applyDeadband(xbox.getLeftX(), 0.1);
    driveSub.setDefaultCommand(driveSub.run(() -> driveSub.arcadeDrive(move.getAsDouble(), turn.getAsDouble())));
    xbox.leftBumper().onTrue(DriveCommands.toggleInverted(driveSub));

    // Intake
    xbox.rightBumper().whileTrue(IntakeCommands.runIntakeToShooter(intakeSub, shooterSub, 1));
    xbox.rightTrigger().whileTrue(IntakeCommands.runIntake(intakeSub, 0.5));

    // Shooter
    shooterArmSub.setDefaultCommand(shooterArmSub.run(() -> shooterArmSub.runShooterActuate(-extreme.getStickY())));
    extreme.trigger.whileTrue(ShooterCommands.runShooter(shooterSub, -0.6));
    extreme.sideButton.whileTrue(ShooterCommands.runShooter(shooterSub, -1));
    extreme.baseFrontLeft.onTrue(ShooterCommands.setPosition(shooterArmSub, Position.Top, 0.6));
    extreme.baseMiddleLeft.onTrue(ShooterCommands.setPosition(shooterArmSub, Position.Handoff, 0.6));
    extreme.baseBackLeft.onTrue(ShooterCommands.setPosition(shooterArmSub, Position.Bottom, 0.6));

    // Climber
    extreme.joystickTopRight.whileTrue(ClimberCommands.runClimberLeft(climberSub, 1));
    extreme.joystickBottomRight.whileTrue(ClimberCommands.runClimberLeft(climberSub, -1));
    extreme.joystickTopLeft.whileTrue(ClimberCommands.runClimberRight(climberSub, 1));
    extreme.joystickBottomLeft.whileTrue(ClimberCommands.runClimberRight(climberSub, -1));
  }

  public Command autonomousCommand() {
  switch (chooser.getSelected()) {

      case BlueFullAuto:
        return (
          DriveCommands.setInverted(driveSub, false)
            .andThen(AutoCommands.autoDriveAndTurn(driveSub, -0.7, -0.56, 1.2))
            .andThen(driveSub.runOnce(()-> driveSub.arcadeDrive(0, 0)))
            .andThen(AutoCommands.autoDriveForward(driveSub, 0.18, 0.3))
            .andThen(driveSub.runOnce(()-> driveSub.arcadeDrive(0, 0)))
            .alongWith(ShooterCommands.runTimedShooterActuate(shooterArmSub, -0.6, 2.4))
            )
            .andThen(ShooterCommands.runShooter(shooterSub, -1).withTimeout(1))
            .andThen(
              ShooterCommands.runTimedShooterActuate(shooterArmSub, 0.7, 2)
                .alongWith(AutoCommands.autoDriveAndTurn(driveSub, 0.5, -0.5, 1.8))
                )
            .andThen(IntakeCommands.runIntakeToShooter(intakeSub, shooterSub, 0.8).withTimeout(2));
           

      case RedFullAuto:
        return (        
          DriveCommands.setInverted(driveSub, false)
            .andThen(AutoCommands.autoDriveAndTurn(driveSub, -0.7, 0.56, 1.2))
            .andThen(driveSub.runOnce(()-> driveSub.arcadeDrive(0, 0)))
            .andThen(AutoCommands.autoDriveForward(driveSub, 0.18, 0.3))
            .andThen(driveSub.runOnce(()-> driveSub.arcadeDrive(0, 0)))
            .alongWith(ShooterCommands.runTimedShooterActuate(shooterArmSub, -0.6, 2.4))
            )
            .andThen(ShooterCommands.runShooter(shooterSub, -1).withTimeout(1))
            .andThen(
              ShooterCommands.runTimedShooterActuate(shooterArmSub, 0.7, 2)
                .alongWith(AutoCommands.autoDriveAndTurn(driveSub, 0.5, 0.5, 1.8))
                )
            .andThen(IntakeCommands.runIntakeToShooter(intakeSub, shooterSub, 0.8).withTimeout(2));

            
      case SpeakerAuto:
        return  AutoCommands.autoDriveForward(driveSub, 0.3, 1);
      

      case StageAuto: 
        return AutoCommands.autoDriveForward(driveSub, 0.4, 1.5);
      

      case DriveForward:
        return AutoCommands.autoDriveForward(driveSub, 0.5, 2);
       

      case None:
      default:
        return new Command(){};
    }
  }
}
