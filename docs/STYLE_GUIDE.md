# FreshHarvest CSS Style Guide

This document outlines the CSS styling system for the FreshHarvest JavaFX application.

## Design Philosophy

- **Clean & Modern**: Minimalist design with subtle shadows and rounded corners
- **Green Theme**: Primary color palette based on fresh harvest greens (#2e7d32, #4caf50)
- **Consistent Spacing**: Standardized padding and margins using 8px base unit
- **Accessibility**: High contrast ratios and clear visual hierarchy

## Color Palette

### Primary Colors
- **Primary Green**: `#2e7d32` (Dark green for headers, brand)
- **Primary Light**: `#4caf50` (Main action color)
- **Primary Lighter**: `#66bb6a` (Hover states)

### Secondary Colors
- **Secondary Light**: `#81c784`
- **Secondary Lighter**: `#a5d6a7`
- **Secondary Lightest**: `#c8e6c9`

### Accent Colors
- **Warning**: `#ff9800`
- **Danger**: `#f44336`
- **Info**: `#2196f3`

### Neutral Colors
- **Background**: `#fafafa`
- **Card Background**: `#ffffff`
- **Border**: `#e0e0e0`
- **Text Primary**: `#424242`
- **Text Secondary**: `#757575`
- **Text Muted**: `#bdbdbd`

## Component Classes

### Buttons

#### Primary Button (Default)
```xml
<Button text="Save" />
```
- Green background with white text
- Subtle drop shadow
- Hover and pressed states

#### Secondary Button
```xml
<Button text="Cancel" styleClass="button-secondary" />
```
- White background with green border and text
- Hover state with light green background

#### Danger Button
```xml
<Button text="Delete" styleClass="button-danger" />
```
- Red background for destructive actions

#### Warning Button
```xml
<Button text="Warning" styleClass="button-warning" />
```
- Orange background for warning actions

#### Button Sizes
```xml
<Button text="Small" styleClass="button-small" />
<Button text="Large" styleClass="button-large" />
<Button text="Icon" styleClass="button-icon" />
```

### Sidebar Navigation
```xml
<Button text="Dashboard" styleClass="active" />
```
- Transparent background by default
- Green highlight for active state
- Left border accent for active items

### Form Controls

#### Text Fields
```xml
<TextField promptText="Enter name..." />
```
- White background with subtle border
- Green focus state with glow effect

#### Text Areas
```xml
<TextArea />
```
- Similar styling to text fields
- Suitable for multi-line input

#### Combo Boxes
```xml
<ComboBox />
```
- Consistent with text field styling

### Cards and Containers

#### Card
```xml
<VBox styleClass="card">
    <Label text="Card Content" />
</VBox>
```
- White background with drop shadow
- Rounded corners
- Standard padding

#### Card with Header/Footer
```xml
<VBox styleClass="card">
    <HBox styleClass="card-header">
        <Label text="Header" />
    </HBox>
    <VBox styleClass="card-body">
        <Label text="Body content" />
    </VBox>
    <HBox styleClass="card-footer">
        <Button text="Action" />
    </HBox>
</VBox>
```

### Typography

#### Page Titles
```xml
<Label text="Dashboard" styleClass="page-title" />
```
- Large, bold green text (32px)

#### Page Subtitles
```xml
<Label text="Subtitle" styleClass="page-subtitle" />
```
- Medium gray text (18px)

#### Section Titles
```xml
<Label text="Section" styleClass="section-title" />
```
- Bold dark gray text (20px)

#### Label Variants
```xml
<Label text="Primary" styleClass="label-primary" />
<Label text="Secondary" styleClass="label-secondary" />
<Label text="Muted" styleClass="label-muted" />
```

### Tables
```xml
<TableView />
```
- Clean borders and headers
- Hover and selection states
- Alternating row colors for better readability

### Alerts and Notifications
```xml
<Label text="Success message" styleClass="alert-success" />
<Label text="Warning message" styleClass="alert-warning" />
<Label text="Error message" styleClass="alert-error" />
<Label text="Info message" styleClass="alert-info" />
```

### Utility Classes

#### Spacing
```xml
<VBox styleClass="spacing-small">   <!-- 8px spacing -->
<VBox styleClass="spacing-medium">  <!-- 16px spacing -->
<VBox styleClass="spacing-large">   <!-- 24px spacing -->
```

#### Padding
```xml
<VBox styleClass="padding-small">   <!-- 8px padding -->
<VBox styleClass="padding-medium">  <!-- 16px padding -->
<VBox styleClass="padding-large">   <!-- 24px padding -->
```

#### Text Alignment
```xml
<Label styleClass="text-center" />
<Label styleClass="text-left" />
<Label styleClass="text-right" />
```

## Usage Examples

### Dashboard Card Layout
```xml
<VBox styleClass="card spacing-medium">
    <Label text="Sales Overview" styleClass="section-title" />
    <HBox styleClass="spacing-small">
        <Label text="Total Sales: " styleClass="label-secondary" />
        <Label text="$12,345" styleClass="label-primary" />
    </HBox>
    <Button text="View Details" />
</VBox>
```

### Form Layout
```xml
<VBox styleClass="card spacing-medium">
    <Label text="Add New Product" styleClass="section-title" />
    <TextField promptText="Product Name" />
    <TextArea promptText="Description" />
    <HBox styleClass="spacing-small">
        <Button text="Save" />
        <Button text="Cancel" styleClass="button-secondary" />
    </HBox>
</VBox>
```

### Navigation Sidebar
```xml
<VBox styleClass="sidebar spacing-small">
    <Label text="FreshHarvest" styleClass="brand" />
    <Separator />
    <Button text="Dashboard" styleClass="active" />
    <Button text="Products" />
    <Button text="Orders" />
    <Button text="Customers" />
    <Button text="Reports" />
</VBox>
```

## Best Practices

1. **Consistent Spacing**: Use the predefined spacing classes for consistent layouts
2. **Color Usage**: Stick to the defined color palette for brand consistency
3. **Button Hierarchy**: Use primary buttons for main actions, secondary for alternatives
4. **Typography Scale**: Use the appropriate text styles for content hierarchy
5. **Card Layout**: Group related content in cards for better organization
6. **Responsive Design**: Consider content overflow and resizing behavior

## Implementation Notes

- All measurements use pixels (px) for consistency
- Drop shadows are subtle (low opacity, small blur radius)
- Hover states provide immediate visual feedback
- Focus states are clearly visible for accessibility
- Color contrast ratios meet accessibility standards