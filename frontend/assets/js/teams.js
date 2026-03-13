/* teams.js */
if (!localStorage.getItem('jwt_token')) window.location.href = 'index.html';
document.getElementById('logoutBtn').addEventListener('click', () => { localStorage.clear(); window.location.href = 'index.html'; });
const av = document.getElementById('avatarInitial');
if (av) av.textContent = (localStorage.getItem('username') || 'A').charAt(0).toUpperCase();

let allTeams = [];
let allStudents = [];
let editingId = null;

async function init() {
    await Promise.all([loadTeams(), loadStudents()]);
}

async function loadTeams() {
    try {
        allTeams = await API.request('/teams');
        renderTable(allTeams);
    } catch (e) {
        document.getElementById('teamsBody').innerHTML =
            '<tr><td colspan="6" style="text-align:center;color:var(--pink);padding:30px;">Could not load teams.</td></tr>';
    }
}

async function loadStudents() {
    try {
        allStudents = await API.request('/students');
        renderStudentCheckboxes();
    } catch (e) { console.error('Failed to load students for selection'); }
}

function renderTable(teams) {
    document.getElementById('totalCount').textContent = teams.length;
    const body = document.getElementById('teamsBody');
    if (!teams.length) {
        body.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--text3);padding:30px;">No teams found.</td></tr>'; return;
    }
    body.innerHTML = teams.map((t, i) => `
        <tr>
            <td class="text-muted" style="font-size:12px;">${i + 1}</td>
            <td><strong>${t.name}</strong></td>
            <td class="text-muted">${t.description || '—'}</td>
            <td><span class="badge badge-purple">${t.memberIds ? t.memberIds.length : 0} members</span></td>
            <td>${t.createdByUsername || 'Admin'}</td>
            <td style="display:flex;gap:6px;">
                <button class="btn btn-outline" style="padding:5px 10px;font-size:11px;" onclick="editTeam(${t.id})"><i class="fa-solid fa-pen"></i></button>
                <button class="btn btn-danger" style="padding:5px 10px;font-size:11px;" onclick="deleteTeam(${t.id})"><i class="fa-solid fa-trash"></i></button>
            </td>
        </tr>`).join('');
}

function renderStudentCheckboxes() {
    const container = document.getElementById('studentCheckboxes');
    if (!allStudents.length) { container.innerHTML = '<div style="font-size:12px;color:var(--text3);">No students available</div>'; return; }
    container.innerHTML = allStudents.map(s => `
        <label style="display:flex;align-items:center;gap:10px;margin-bottom:8px;cursor:pointer;font-size:13px;">
            <input type="checkbox" name="members" value="${s.id}" style="width:16px;height:16px;accent-color:var(--cyan);">
            ${s.fullName} (${s.rollNumber})
        </label>
    `).join('');
}

function openModal(id = null) {
    editingId = id;
    document.getElementById('modalTitle').textContent = id ? 'EDIT TEAM' : 'CREATE TEAM';
    document.getElementById('teamForm').reset();
    document.querySelectorAll('.input-group').forEach(g => g.classList.remove('invalid'));

    if (id) {
        const t = allTeams.find(x => x.id === id);
        if (t) {
            document.getElementById('teamName').value = t.name;
            document.getElementById('description').value = t.description || '';
            const checks = document.querySelectorAll('input[name="members"]');
            checks.forEach(c => {
                c.checked = t.memberIds && t.memberIds.includes(parseInt(c.value));
            });
        }
    }
    document.getElementById('teamModal').classList.add('active');
}

function closeModal() { document.getElementById('teamModal').classList.remove('active'); editingId = null; }

document.getElementById('teamForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('teamName').value.trim();
    if (!name) { document.getElementById('teamName').closest('.input-group').classList.add('invalid'); return; }

    const selectedMembers = Array.from(document.querySelectorAll('input[name="members"]:checked')).map(c => parseInt(c.value));

    const payload = {
        name,
        description: document.getElementById('description').value.trim(),
        memberIds: selectedMembers
    };

    try {
        const btn = document.getElementById('saveTeamBtn');
        btn.disabled = true;
        btn.innerHTML = '<div class="spinner active"></div> Saving...';

        if (editingId) await API.request('/teams/' + editingId, { method: 'PUT', body: JSON.stringify(payload) });
        else await API.request('/teams', { method: 'POST', body: JSON.stringify(payload) });

        closeModal();
        loadTeams();
    } catch (err) {
        alert(err.data?.message || 'Failed to save team.');
    } finally {
        const btn = document.getElementById('saveTeamBtn');
        btn.disabled = false;
        btn.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Save Team';
    }
});

function editTeam(id) { openModal(id); }

async function deleteTeam(id) {
    if (!confirm('Delete this team?')) return;
    try { await API.request('/teams/' + id, { method: 'DELETE' }); loadTeams(); }
    catch (e) { alert('Failed to delete team.'); }
}

init();
