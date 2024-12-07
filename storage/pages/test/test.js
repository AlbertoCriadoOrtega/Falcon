// script.js
let h1Element = document.getElementById("moveable");

let moveRight = true;

function moveH1() {
    if (moveRight) {
        // Move right
        h1Element.style.transform = "translateX(200px)";
    } else {
        // Move left
        h1Element.style.transform = "translateX(-200px)";
    }

    // Toggle the direction
    moveRight = !moveRight;
}

// Set an interval to move the h1 every 1 second
setInterval(moveH1, 1000);
