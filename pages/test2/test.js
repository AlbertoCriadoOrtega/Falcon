// script.js
let h1Element = document.getElementById("moveable");

let moveRight = true;
let colorToggle = true;
let rotationAngle = 0;

function moveH1() {
    // Move the h1 element left or right
    if (moveRight) {
        h1Element.style.transform = "translateX(200px)";
    } else {
        h1Element.style.transform = "translateX(-200px)";
    }

    // Toggle the direction
    moveRight = !moveRight;
}

function changeColor() {
    // Toggle between two colors
    if (colorToggle) {
        h1Element.style.color = "red";
    } else {
        h1Element.style.color = "blue";
    }

    // Toggle the color
    colorToggle = !colorToggle;
}

function rotateH1() {
    // Increment the rotation angle
    rotationAngle += 45;
    if (rotationAngle >= 360) {
        rotationAngle = 0; // Reset rotation after a full circle
    }

    // Apply the rotation along with any previous transformations
    h1Element.style.transform += ` rotate(${rotationAngle}deg)`;
}

// Set an interval to move the h1 every 1 second
setInterval(moveH1, 1000);

// Set an interval to change color every 1.5 seconds
setInterval(changeColor, 1500);

// Set an interval to rotate the h1 every 2 seconds
setInterval(rotateH1, 2000);

// Add smooth transition effect for transformations
h1Element.style.transition = "transform 0.5s ease, color 0.5s ease";
