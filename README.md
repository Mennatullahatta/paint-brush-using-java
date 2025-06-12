# Paint Brush Application

A Java-based drawing application that allows users to create basic shapes with different colors and line strokes. The application supports various drawing tools, undo functionality, and file operations.

## Features

- **Drawing Tools**:
  - Rectangle, Oval, Line
  - Freehand Pencil
  - Eraser
- **Color Options**: Red, Green, Blue
- **Shape Customization**:
  - Dotted or solid strokes
  - Filled or outlined shapes
- **File Operations**:
  - Save drawings as PNG images
  - Open existing images
- **Editing Tools**:
  - Clear entire canvas
  - Undo previous actions

## Object-Oriented Programming Principles

The application demonstrates several OOP principles:

1. **Abstraction**:
   - Abstract `Shape` class defines common properties and methods
   - Hides implementation details of different shapes

2. **Inheritance**:
   - `MyLine`, `MyRectangle`, and `MyOval` classes extend the abstract `Shape` class
   - Inherit common properties while implementing shape-specific behavior

3. **Polymorphism**:
   - Each shape implements its own `draw()` and `contains()` methods
   - The drawing panel treats all shapes uniformly through the abstract `Shape` interface

4. **Encapsulation**:
   - Shape properties are private with public getters
   - DrawingPanel manages its internal state (shapes, colors, tools) privately

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or later installed

### Running the Application
1. **Compile and Run from Source**:
   ```bash
   javac PaintBrush.java
   java PaintBrush
