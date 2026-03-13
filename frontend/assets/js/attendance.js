/* attendance.js */
if (!localStorage.getItem('jwt_token')) window.location.href = 'index.html';
document.getElementById('logoutBtn').addEventListener('click', () => { localStorage.clear(); window.location.href = 'index.html'; });

async function loadEvents() {
    try {
        const events = await API.request('/events');
        const filter = document.getElementById('eventFilter');
        const markEvent = document.getElementById('markEvent');
        events.forEach(e => {
            filter.appendChild(new Option(e.title, e.id));
            markEvent.appendChild(new Option(e.title, e.id));
        });
    } catch (e) { console.error(e); }
}

async function loadStudents() {
    try {
        const students = await API.request('/students');
        const sel = document.getElementById('markStudent');
        students.forEach(s => sel.appendChild(new Option(s.fullName + ' (' + s.rollNumber + ')', s.id)));
    } catch (e) { console.error(e); }
}

async function loadAttendance() {
    const eventId = document.getElementById('eventFilter').value;
    const body = document.getElementById('attendanceBody');

    if (!eventId) {
        body.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--text3);padding:40px;">Select an event to view attendance.</td></tr>';
        return;
    }

    try {
        const records = await API.request('/attendance/event/' + eventId);
        const present = records.filter(r => r.isPresent === true || r.present === true).length;
        const absent = records.length - present;

        document.getElementById('totalPresent').textContent = present;
        document.getElementById('totalAbsent').textContent = absent;
        document.getElementById('attendanceRate').textContent = records.length ? Math.round((present / records.length) * 100) + '%' : '—';

        if (!records.length) {
            body.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--text3);padding:30px;">No records for this event.</td></tr>';
            return;
        }

        body.innerHTML = records.map((r, i) => `
            <tr>
                <td class="text-muted">${i + 1}</td>
                <td><strong>${r.studentName || '—'}</strong></td>
                <td class="text-cyan">${r.studentRollNumber || '—'}</td>
                <td>${r.eventTitle || '—'}</td>
                <td><span class="badge ${(r.isPresent || r.present) ? 'badge-green' : 'badge-pink'}">${(r.isPresent || r.present) ? 'PRESENT' : 'ABSENT'}</span></td>
                <td class="text-muted">${r.recordedAt ? new Date(r.recordedAt).toLocaleString() : '—'}</td>
                <td><button class="btn btn-danger" style="padding:5px 10px;font-size:10px;" onclick="deleteRecord(${r.id})"><i class="fa-solid fa-trash"></i></button></td>
            </tr>`).join('');
    } catch (e) {
        console.error(e);
        body.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--pink);padding:30px;">Error loading data</td></tr>';
    }
}

function openMarkModal() { document.getElementById('markModal').classList.add('active'); }
function closeMarkModal() { document.getElementById('markModal').classList.remove('active'); }

document.getElementById('attendanceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        eventId: parseInt(document.getElementById('markEvent').value),
        studentId: parseInt(document.getElementById('markStudent').value),
        isPresent: document.getElementById('markStatus').value === 'PRESENT'
    };

    try {
        await API.request('/attendance', { method: 'POST', body: JSON.stringify(payload) });
        closeMarkModal();
        loadAttendance();
    } catch (err) { alert('Failed to mark attendance.'); }
});

async function deleteRecord(id) {
    if (!confirm('Remove this attendance record?')) return;
    try {
        await API.request('/attendance/' + id, { method: 'DELETE' });
        loadAttendance();
    } catch (e) { alert('Failed.'); }
}

loadEvents(); loadStudents();
