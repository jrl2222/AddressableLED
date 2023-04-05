// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

public class Robot extends TimedRobot {
  private AddressableLED m_led;
  private AddressableLEDBuffer m_ledBuffer;
  // Store what the last hue of the first pixel is
  private int m_rainbowFirstPixelHue;
  private final TimeOfFlight _tof    = new TimeOfFlight(102);

  @Override
  public void robotInit() {
    // PWM port 9
    // Must be a PWM header, not MXP or DIO
    m_led = new AddressableLED(9);
    
    m_ledBuffer = new AddressableLEDBuffer(107);
    m_led.setLength(m_ledBuffer.getLength());
    _tof.setRangingMode    ( RangingMode.Short, 24.0d );
    
    // Set the data
    m_led.setData(m_ledBuffer);
    m_led.start();
  }

  @Override
  public void robotPeriodic() {
    // Fill the buffer with a rainbow
    rainbow();
    // Set the LEDs
    m_led.setData(m_ledBuffer);
    SmartDashboard.putNumber("_tof", _tof.getRange()  );
    
  }

  private void rainbow() {
    // For every pixel
    for (var i = 0; i < m_ledBuffer.getLength(); i++) {
      // Calculate the hue - hue is easier for rainbows because the color
      // shape is a circle so only one value needs to precess
      final var hue = (m_rainbowFirstPixelHue + (i * 180 / m_ledBuffer.getLength())) % 180;
      // Set the value
      if (_tof.getRange()  <1000){
        m_ledBuffer.setHSV(i, hue, 255, 128);
      }else{
        
      m_ledBuffer.setHSV(i, 0, 0, 0);
      }
      
    }
    // Increase by to make the rainbow "move"
    m_rainbowFirstPixelHue += 3;
    // Check bounds
    m_rainbowFirstPixelHue %= 180;
  }
}




package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

public class Robot extends TimedRobot {
  long _ledTimer;
  private AddressableLED m_led;
  private AddressableLEDBuffer m_ledBuffer;
  private int m_rainbowFirstPixelHue;
  private final TimeOfFlight _tof    = new TimeOfFlight(102);
  int x = 0;
  boolean up = true;
  double minRead = 50;
  double maxRead = 1500;
  double avTime = 0;
  double prevtime = System.currentTimeMillis();

  @Override
  public void robotInit() {
    // Must be a PWM header, not MXP or DIO
    m_led = new AddressableLED(9);
    m_ledBuffer = new AddressableLEDBuffer(107);
    m_led.setLength(m_ledBuffer.getLength());
    _tof.setRangingMode    ( RangingMode.Short, 24.0d );
    m_led.setData(m_ledBuffer);
    m_led.start();
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("_tof", _tof.getRange()  );
    //double rob_Distance = (_tof.getRange() < minRead ||_tof.getRange() > maxRead)? maxRead : _tof.getRange();
    m_led.setData(m_ledBuffer);
  }

  @Override
  public void teleopPeriodic(){
    newDistance();
  }

  @Override
  public void disabledInit(){
    x=0;
  }
  @Override
  public void disabledPeriodic(){
    startUp();
  }

  private void newDistance(){
    double rob_Distance = (_tof.getRange() < minRead ||_tof.getRange() > maxRead)? maxRead : _tof.getRange();
    float percent = (float) ((rob_Distance - minRead)/(maxRead-minRead));
    SmartDashboard.putNumber("Rob", rob_Distance);
    int numOflEDS = Math.round((float) m_ledBuffer.getLength() * (1-percent));
    

    // Turn the leds red as we get closer
    for (int i = 0; i < numOflEDS; i++) {
      m_ledBuffer.setRGB(i, 255, 0, 0);
    }
    
    // Have the remaining leds green
    for (int i = numOflEDS; i < m_ledBuffer.getLength(); i++) {
      m_ledBuffer.setRGB(i, 0, 0, 0);
    }
    

  }

  private void distance(){
    double rob_Distance = _tof.getRange() /1000;
    
   
    
    int numLeds = (int) (rob_Distance * m_ledBuffer.getLength() );
    
    numLeds = Math.min(numLeds, m_ledBuffer.getLength());
    numLeds = Math.max(numLeds, 0);

    // Turn the leds red as we get closer
    for (int i = 0; i < numLeds; i++) {
      m_ledBuffer.setRGB(i, 255, 0, 0);
    }
    
    // Have the remaining leds green
    for (int i = numLeds; i < m_ledBuffer.getLength(); i++) {
      m_ledBuffer.setRGB(i, 0, 255, 0);
    }
  }

  private void startUp(){
    if (up){
        m_ledBuffer.setLED(x, Color.kPurple);
        x++;
        if(x>=106){up = false;}      
    }else{
      if (!up){
          m_ledBuffer.setLED(x, Color.kRed);
          x--;
          if(x<=1){up = true;}
      }
    }
  }


  @Override
  public void testPeriodic() {
    // For every pixel
    for (var i = 0; i < m_ledBuffer.getLength(); i++) {
      // Calculate the hue - hue is easier for rainbows because the color
      // shape is a circle so only one value needs to precess
      final var hue = (m_rainbowFirstPixelHue + (i * 180 / m_ledBuffer.getLength())) % 180;
      // Set the value
      if (_tof.getRange()  <400){
        m_ledBuffer.setHSV(i, hue, 255, 128);
      }else{
        m_ledBuffer.setLED(i, Color.kRed);
      //m_ledBuffer.setHSV(i, 255, 255, 0);
      }
      
    }
    // Increase by to make the rainbow "move"
    m_rainbowFirstPixelHue += 3;
    // Check bounds
    m_rainbowFirstPixelHue %= 180;
  }

}

