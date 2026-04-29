const state = {
  user: null,
  projects: [],
  project: null,
  members: [],
  signup: false
};

const $ = (id) => document.getElementById(id);

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    let message = "Request failed";
    try {
      message = (await response.json()).message || message;
    } catch {
      message = response.statusText;
    }
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}

function showToast(message) {
  $("toast").textContent = message;
  $("toast").classList.remove("hidden");
  setTimeout(() => $("toast").classList.add("hidden"), 2600);
}

async function loadMe() {
  try {
    state.user = await api("/api/auth/me");
    $("authView").classList.add("hidden");
    $("appView").classList.remove("hidden");
    $("currentUser").textContent = `${state.user.name} (${state.user.email})`;
    await loadProjects();
  } catch {
    $("authView").classList.remove("hidden");
    $("appView").classList.add("hidden");
  }
}

async function loadProjects() {
  state.projects = await api("/api/projects");
  $("projects").innerHTML = state.projects.map(project => `
    <button class="project-btn ${state.project?.id === project.id ? "active" : ""}" data-project="${project.id}">
      ${project.name}${project.admin ? " - Admin" : ""}
    </button>
  `).join("");
}

async function selectProject(id) {
  state.project = state.projects.find(project => project.id === id);
  $("projectTitle").textContent = state.project.name;
  $("projectDescriptionText").textContent = state.project.description || "";
  $("memberPanel").classList.remove("hidden");
  $("taskPanel").classList.remove("hidden");
  await Promise.all([loadMembers(), loadTasks(), loadDashboard()]);
  await loadProjects();
}

async function loadMembers() {
  state.members = await api(`/api/projects/${state.project.id}/members`);
  $("memberForm").classList.toggle("hidden", !state.project.admin);
  $("members").innerHTML = state.members.map(member => `
    <div class="member">
      <span>${member.name}<br><small>${member.email} - ${member.role}</small></span>
      ${state.project.admin && member.userId !== state.user.id ? `<button class="ghost" data-remove-member="${member.userId}">Remove</button>` : ""}
    </div>
  `).join("");
  $("taskAssignee").innerHTML = `<option value="">Unassigned</option>` + state.members.map(member => `
    <option value="${member.userId}">${member.name}</option>
  `).join("");
}

async function loadTasks() {
  const tasks = await api(`/api/projects/${state.project.id}/tasks`);
  $("taskForm").classList.toggle("hidden", !state.project.admin);
  $("tasks").innerHTML = tasks.map(task => `
    <article class="task">
      <div>
        <h3>${task.title}</h3>
        <p>${task.description || ""}</p>
        <small>${task.priority} priority - ${task.assigneeName} ${task.dueDate ? `- due ${task.dueDate}` : ""}</small>
      </div>
      <select data-status="${task.id}">
        <option value="TODO" ${task.status === "TODO" ? "selected" : ""}>To Do</option>
        <option value="IN_PROGRESS" ${task.status === "IN_PROGRESS" ? "selected" : ""}>In Progress</option>
        <option value="DONE" ${task.status === "DONE" ? "selected" : ""}>Done</option>
      </select>
    </article>
  `).join("");
}

async function loadDashboard() {
  const dashboard = await api(`/api/projects/${state.project.id}/dashboard`);
  $("dashboard").innerHTML = `
    <div class="metric"><strong>${dashboard.totalTasks}</strong><span>Total tasks</span></div>
    <div class="metric"><strong>${dashboard.tasksByStatus.TODO || 0}</strong><span>To Do</span></div>
    <div class="metric"><strong>${dashboard.tasksByStatus.IN_PROGRESS || 0}</strong><span>In Progress</span></div>
    <div class="metric"><strong class="${dashboard.overdueTasks ? "danger" : ""}">${dashboard.overdueTasks}</strong><span>Overdue</span></div>
  `;
}

$("toggleAuth").addEventListener("click", () => {
  state.signup = !state.signup;
  $("authTitle").textContent = state.signup ? "Create account" : "Sign in";
  $("authSubmit").textContent = state.signup ? "Create account" : "Sign in";
  $("toggleAuth").textContent = state.signup ? "Use existing account" : "Create an account";
  $("nameField").classList.toggle("hidden", !state.signup);
});

$("authForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const payload = { email: $("email").value, password: $("password").value };
    if (state.signup) payload.name = $("name").value;
    state.user = await api(state.signup ? "/api/auth/signup" : "/api/auth/login", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    await loadMe();
  } catch (error) {
    showToast(error.message);
  }
});

$("projectForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await api("/api/projects", {
      method: "POST",
      body: JSON.stringify({ name: $("projectName").value, description: $("projectDescription").value })
    });
    event.target.reset();
    await loadProjects();
  } catch (error) {
    showToast(error.message);
  }
});

$("projects").addEventListener("click", async (event) => {
  const button = event.target.closest("[data-project]");
  if (button) await selectProject(Number(button.dataset.project));
});

$("memberForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await api(`/api/projects/${state.project.id}/members`, {
      method: "POST",
      body: JSON.stringify({ email: $("memberEmail").value, role: $("memberRole").value })
    });
    event.target.reset();
    await loadMembers();
  } catch (error) {
    showToast(error.message);
  }
});

$("members").addEventListener("click", async (event) => {
  const button = event.target.closest("[data-remove-member]");
  if (!button) return;
  await api(`/api/projects/${state.project.id}/members/${button.dataset.removeMember}`, { method: "DELETE" });
  await loadMembers();
});

$("taskForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await api(`/api/projects/${state.project.id}/tasks`, {
      method: "POST",
      body: JSON.stringify({
        title: $("taskTitle").value,
        description: $("taskDescription").value,
        dueDate: $("taskDueDate").value || null,
        priority: $("taskPriority").value,
        assigneeId: $("taskAssignee").value ? Number($("taskAssignee").value) : null
      })
    });
    event.target.reset();
    await Promise.all([loadTasks(), loadDashboard()]);
  } catch (error) {
    showToast(error.message);
  }
});

$("tasks").addEventListener("change", async (event) => {
  if (!event.target.matches("[data-status]")) return;
  await api(`/api/tasks/${event.target.dataset.status}/status`, {
    method: "PUT",
    body: JSON.stringify({ status: event.target.value })
  });
  await loadDashboard();
});

$("logout").addEventListener("click", async () => {
  await api("/api/auth/logout", { method: "POST" });
  location.reload();
});

loadMe();
