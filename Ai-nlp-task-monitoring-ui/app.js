import { main as processWithAI } from './openai-integration.js';

// Check authentication on page load
(function checkAuth() {
    const userData = localStorage.getItem('userData');
    if (!userData) {
        window.location.replace('login.html');
        return;
    }
})();

document.addEventListener('DOMContentLoaded', () => {
    const userData = localStorage.getItem('userData');
    if (!userData) {
        window.location.replace('login.html');
        return;
    }

    const voiceButton = document.querySelector('.voice-btn');
    if (voiceButton) {
        voiceButton.addEventListener('click', startListening);
    }
    //changed
    clearTaskOutput();
    updateTaskList();

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('userData');
            
            // Redirect to login page
            window.location.href = 'login.html';
        });
    }
});

function getUserData() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

//changed
function clearTaskOutput() {
    const taskInfo = document.querySelector('.task-info');
    if (taskInfo) {
        document.getElementById('operation').textContent = '';
        document.getElementById('task').textContent = '';
        document.getElementById('urgency').textContent = '';

        //changed
        document.getElementById('datetime').textContent = '';
    }
    const confirmationArea = document.getElementById('confirmation-area');
    if (confirmationArea) {
        confirmationArea.innerHTML = '';
    }
}

function startListening() {
    if ('webkitSpeechRecognition' in window) {
        const recognition = new webkitSpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-US';

        recognition.onstart = function () {
            console.log("Listening...");
            clearTaskOutput();
        };

        recognition.onresult = function (event) {
            const transcript = event.results[0][0].transcript;
            processVoiceCommand(transcript);
        };

        recognition.onerror = function (event) {
            console.error("Speech recognition error:", event.error);
        };

        recognition.start();
    } else {
        alert("Speech recognition not supported in this browser.");
    }
}

function getUrgencyColor(urgency) {
    if (urgency == null)
        return
    switch (urgency.toLowerCase()) {
        case 'high':
            return '#FF0000';
        case 'medium':
            return '#FFA500';
        case 'low':
            return '#008000';
        default:
            return '#808080';
    }
}

function updateTaskList() {
    const todoList = document.getElementById("todo-list");
    todoList.innerHTML = '';
    
    getTasksFromDb().then(tasks => {
        tasks.forEach((taskData, index) => {
            const listItem = document.createElement("div");
            listItem.classList.add('todo-item');

            const statusIndicator = document.createElement("div");
            statusIndicator.classList.add('status-indicator');
            statusIndicator.style.backgroundColor = getUrgencyColor(taskData.urgency);

            const taskContent = document.createElement("div");
            taskContent.classList.add('task-content');

            const taskTitle = document.createElement("div");
            taskTitle.classList.add('task-title');
            taskTitle.innerHTML = `
                <span class="operation-badge" style="background-color: ${getUrgencyColor(taskData.urgency)}">${taskData.operation}</span>
                <span class="task-name">${taskData.task}</span>
            `;

            const taskDetails = document.createElement("div");
            taskDetails.classList.add('task-details-line');
            taskDetails.innerHTML = `
                <span class="urgency-badge" style="background-color: ${getUrgencyColor(taskData.urgency)}">${taskData.urgency}</span>
                ${taskData.dateTime ? `<span class="datetime">${taskData.dateTime}</span>` : ''}
            `;

            taskContent.appendChild(taskTitle);
            taskContent.appendChild(taskDetails);

            const completeButton = document.createElement("button");
            completeButton.classList.add('complete-btn');
            completeButton.innerHTML = '';
            completeButton.title = 'Mark as Completed';
            completeButton.onclick = async () => {
                try {
                    const response = await fetch(`http://localhost:9090/api/tasks/${taskData.id}`, {
                        method: 'DELETE',
                        headers: {
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        }
                    });

                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }

                    updateTaskList();
                } catch (error) {
                    console.error("Error deleting task:", error);
                    alert("Failed to delete task. Please try again.");
                }
            };

            listItem.appendChild(statusIndicator);
            listItem.appendChild(taskContent);
            listItem.appendChild(completeButton);

            todoList.appendChild(listItem);
        });
    }).catch(error => {
        console.error("Error updating task list:", error);
    });
}

async function processVoiceCommand(command) {
    try {
        console.log(command)
        const aiResp = await processWithAI(command);
        const aiResponse = JSON.parse(aiResp.choices[0].message.content)
        console.log(aiResponse)
        //new
        const userData = getUserData();
        if (!userData) {
            throw new Error('User not authenticated');
        }

        const requestBody = {
            operation: aiResponse.operation,
            task: aiResponse.task,
            urgency: aiResponse.urgency,
            datetime: aiResponse.datetime,
            ////////
            userId: userData.id
        }

        var operation, task, urgency, dateTime;
        operation = document.getElementById("operation");
        task = document.getElementById("task");
        urgency = document.getElementById("urgency");
        //changed
        dateTime = document.getElementById("datetime")
        if (!(aiResponse == null || aiResponse.operation == null)) {
            operation.textContent = aiResponse.operation;
        } else {
            console.log("Got null 1");
        }
        if (!(aiResponse == null || aiResponse.task == null)) {
            task.textContent = aiResponse.task;
        }
        else {
            console.log("Got null 2");
        }
        if (!(aiResponse == null || aiResponse.urgency == null)) {
            urgency.textContent = aiResponse.urgency;
        }
        else {
            console.log("Got null 3");
        }
        //changed
        if (!(aiResponse == null || aiResponse.datetime == null)) {
            dateTime.textContent = aiResponse.datetime;
        }
        else {
            console.log("Got null 4");
        }

        const confirmationArea = document.getElementById("confirmation-area")
        confirmationArea.innerHTML = `
            <div class="confirmation-button">
                <p> Is it correct </p>
                <button onclick="window.confirmTask(true)" class="confirm-btn"> YES </button>
                <button onclick="window.confirmTask(fals)" class="confirm-btn"> NO </button> 
            </div>
        `;

        window.confirmTask = async (isCorrect) => {
            if (isCorrect) {
                console.log("Correct");
                confirmationArea.innerHTML = '';
                operation.innerHTML = '';
                task.innerHTML = '';
                urgency.innerHTML = '';
                dateTime.innerHTML = '';
                const response = await fetch("http://localhost:9090/api/tasks", {
                    method: "POST",
                    headers: {
                        "Content-type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify(requestBody)
                });

                if (!response.ok) {
                    console.log("Request unsuccessful")
                    throw new Error(`Http error with status code: ${response.status}`);
                }

                const responseData = await response.json();

                return responseData;
            }
            else {
                console.log("User Told not correct");
                confirmationArea.innerHTML = '';
                operation.innerHTML = '';
                task.innerHTML = '';
                urgency.innerHTML = '';
                dateTime.innerHTML = '';
                startListening();
            }
        }


    }
    catch (error) {
        console.error("Error processing voice command", error)
        startListening()
        return null;
    }
}

async function getTasksFromDb(userId) {
    try {
        const userData = getUserData();
        if (!userData) {
            throw new Error('User not authenticated');
        }

        const response = await fetch(`http://localhost:9090/api/tasks?userId=${userData.id}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return Array.isArray(data) ? data : []; // Always return an array
    } catch (error) {
        console.error("Error fetching tasks:", error);
        return []; 
    }
}