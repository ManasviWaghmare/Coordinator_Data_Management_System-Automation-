/* dashboard.js */
if (!localStorage.getItem('jwt_token')) window.location.href = 'index.html';

// Set user info in sidebar
const username = localStorage.getItem('username') || 'Admin';
const el = document.getElementById('sidebarUsername');
const av = document.getElementById('avatarInitial');
if (el) el.textContent = username;
if (av) av.textContent = username.charAt(0).toUpperCase();

document.getElementById('logoutBtn').addEventListener('click', () => { localStorage.clear(); window.location.href = 'index.html'; });

async function loadDashboard() {
    try {
        // Load all stats in parallel
        const [students, events, teams, attendance] = await Promise.allSettled([
            API.request('/students'),
            API.request('/events'),
            API.request('/teams'),
            API.request('/attendance')
        ]);

        const s = students.status === 'fulfilled' ? students.value : [];
        const e = events.status === 'fulfilled' ? events.value : [];
        const t = teams.status === 'fulfilled' ? teams.value : [];
        const a = attendance.status === 'fulfilled' ? attendance.value : [];

        // Update stat cards
        document.getElementById('statStudents').textContent = s.length || '0';
        document.getElementById('statEvents').textContent = e.length || '0';
        document.getElementById('statTeams').textContent = t.length || '0';
        document.getElementById('statAttendance').textContent = a.length || '0';

        // Recent students table
        renderRecentStudents(s.slice(0, 5));

        // Upcoming events
        renderUpcomingEvents(e.slice(0, 4));

        // Chart
        renderAttendanceChart(e.slice(0, 7), a);

    } catch (err) { console.error('Dashboard error:', err); }
}

function renderRecentStudents(students) {
    const body = document.getElementById('recentStudentsBody');
    if (!students.length) { body.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--text3);padding:30px;">No students found.</td></tr>'; return; }
    body.innerHTML = students.map((s, i) => `
        <tr>
            <td class="text-muted">${i + 1}</td>
            <td><strong>${s.fullName || s.username || '—'}</strong></td>
            <td class="text-muted">${s.email || '—'}</td>
            <td>${s.department || '—'}</td>
            <td>${s.registrationYear || '—'}</td>
            <td><span class="badge badge-cyan">Active</span></td>
        </tr>`).join('');
}

function renderUpcomingEvents(events) {
    const container = document.getElementById('upcomingEvents');
    const colors = ['cyan', 'purple', 'pink', 'cyan'];
    if (!events.length) { container.innerHTML = '<div style="text-align:center;color:var(--text3);font-size:13px;padding:20px;">No upcoming events</div>'; return; }
    container.innerHTML = events.map((ev, i) => `
        <div class="event-item">
            <div class="event-dot ${colors[i % colors.length]}"></div>
            <div>
                <div class="event-name">${ev.title}</div>
                <div class="event-date">${ev.eventDate ? new Date(ev.eventDate).toLocaleDateString() : '—'} • ${ev.location || 'TBD'}</div>
            </div>
        </div>`).join('');
}

function renderAttendanceChart(events, attendance) {
    const ctx = document.getElementById('attendanceChart').getContext('2d');
    const labels = events.map(e => e.title?.substring(0, 16) + '...' || 'Event');
    const data = events.map(e => attendance.filter(a => a.eventId === e.id && a.status === 'PRESENT').length);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Present',
                data: data.length ? data : [12, 19, 8, 15, 22, 10, 17],
                backgroundColor: 'rgba(0, 243, 255, 0.2)',
                borderColor: '#00f3ff',
                borderWidth: 2,
                borderRadius: 4,
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: '#8892b0', font: { size: 10 } }, grid: { color: 'rgba(0,243,255,0.05)' } },
                y: { ticks: { color: '#8892b0', font: { size: 10 } }, grid: { color: 'rgba(0,243,255,0.05)' }, beginAtZero: true }
            }
        }
    });
}

loadDashboard();
