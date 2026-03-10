"use client";

import { useState } from "react";
import Link from "next/link";

/** Demo crew data for UI rendering before backend is connected. */
const DEMO_CREW = [
    {
        id: "1", staffId: "SKY-001", firstName: "Budi", lastName: "Hartono",
        crewRole: "CAPTAIN", crewCategory: "FLIGHT_CREW", baseStation: "CGK",
        status: "ACTIVE", groundingReason: null,
        fullName: "Budi Hartono", documents: [],
        createdAt: "2026-01-15T08:00:00Z", updatedAt: "2026-03-01T10:00:00Z",
    },
    {
        id: "2", staffId: "SKY-002", firstName: "Siti", lastName: "Rahayu",
        crewRole: "FIRST_OFFICER", crewCategory: "FLIGHT_CREW", baseStation: "CGK",
        status: "ACTIVE", groundingReason: null,
        fullName: "Siti Rahayu", documents: [],
        createdAt: "2026-01-20T08:00:00Z", updatedAt: "2026-03-02T10:00:00Z",
    },
    {
        id: "3", staffId: "SKY-003", firstName: "Wayan", lastName: "Putra",
        crewRole: "PURSER", crewCategory: "CABIN_CREW", baseStation: "DPS",
        status: "GROUNDED", groundingReason: "MEDEX expired",
        fullName: "Wayan Putra", documents: [],
        createdAt: "2026-02-01T08:00:00Z", updatedAt: "2026-03-05T10:00:00Z",
    },
    {
        id: "4", staffId: "SKY-004", firstName: "Dewi", lastName: "Lestari",
        crewRole: "FLIGHT_ATTENDANT", crewCategory: "CABIN_CREW", baseStation: "SUB",
        status: "ACTIVE", groundingReason: null,
        fullName: "Dewi Lestari", documents: [],
        createdAt: "2026-02-10T08:00:00Z", updatedAt: "2026-03-06T10:00:00Z",
    },
    {
        id: "5", staffId: "SKY-005", firstName: "Ahmad", lastName: "Rizki",
        crewRole: "CAPTAIN", crewCategory: "FLIGHT_CREW", baseStation: "CGK",
        status: "ON_LEAVE", groundingReason: null,
        fullName: "Ahmad Rizki", documents: [],
        createdAt: "2026-02-15T08:00:00Z", updatedAt: "2026-03-08T10:00:00Z",
    },
];

const ROLE_OPTIONS = [
    { value: "", label: "All Roles" },
    { value: "CAPTAIN", label: "Captain" },
    { value: "FIRST_OFFICER", label: "First Officer" },
    { value: "PURSER", label: "Purser" },
    { value: "FLIGHT_ATTENDANT", label: "Flight Attendant" },
];

const STATUS_OPTIONS = [
    { value: "", label: "All Status" },
    { value: "ACTIVE", label: "Active" },
    { value: "GROUNDED", label: "Grounded" },
    { value: "ON_LEAVE", label: "On Leave" },
];

function StatusBadge({ status }: { status: string }): React.JSX.Element {
    const classMap: Record<string, string> = {
        ACTIVE: "badge-active",
        GROUNDED: "badge-grounded",
        ON_LEAVE: "badge-on-leave",
    };
    const dotColor: Record<string, string> = {
        ACTIVE: "var(--green-500)",
        GROUNDED: "var(--red-500)",
        ON_LEAVE: "var(--amber-500)",
    };

    return (
        <span className={`badge ${classMap[status] || ""}`}>
            <span style={{ width: 6, height: 6, borderRadius: "50%", background: dotColor[status] || "gray" }} />
            {status.replace("_", " ")}
        </span>
    );
}

function RoleBadge({ role, category }: { role: string; category: string }): React.JSX.Element {
    const isFlightCrew = category === "FLIGHT_CREW";
    return (
        <span
            style={{
                display: "inline-flex", alignItems: "center", gap: 6,
                padding: "4px 10px", borderRadius: 6, fontSize: 12, fontWeight: 500,
                background: isFlightCrew ? "var(--sky-50)" : "rgba(168,85,247,0.08)",
                color: isFlightCrew ? "var(--sky-700)" : "#7c3aed",
                border: `1px solid ${isFlightCrew ? "var(--sky-200)" : "rgba(168,85,247,0.2)"}`,
            }}
        >
            {isFlightCrew ? "🛩️" : "👥"} {role.replace("_", " ")}
        </span>
    );
}

export default function CrewListPage(): React.JSX.Element {
    const [roleFilter, setRoleFilter] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [search, setSearch] = useState("");
    const [showAddModal, setShowAddModal] = useState(false);

    const filteredCrew = DEMO_CREW.filter((c) => {
        if (roleFilter && c.crewRole !== roleFilter) return false;
        if (statusFilter && c.status !== statusFilter) return false;
        if (search) {
            const q = search.toLowerCase();
            return (
                c.firstName.toLowerCase().includes(q) ||
                c.lastName.toLowerCase().includes(q) ||
                c.staffId.toLowerCase().includes(q)
            );
        }
        return true;
    });

    return (
        <div className="animate-fade-in-up">
            {/* Toolbar */}
            <div
                style={{
                    display: "flex", justifyContent: "space-between", alignItems: "center",
                    marginBottom: 20, gap: 16, flexWrap: "wrap",
                }}
            >
                <div style={{ display: "flex", gap: 12, flex: 1 }}>
                    {/* Search */}
                    <div style={{ position: "relative", flex: 1, maxWidth: 360 }}>
                        <span style={{ position: "absolute", left: 12, top: "50%", transform: "translateY(-50%)", fontSize: 16, color: "var(--slate-400)" }}>
                            🔍
                        </span>
                        <input
                            id="crew-search"
                            className="input"
                            placeholder="Search by name or staff ID..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            style={{ paddingLeft: 40 }}
                        />
                    </div>

                    {/* Role Filter */}
                    <select
                        id="filter-role"
                        className="input"
                        style={{ width: 180 }}
                        value={roleFilter}
                        onChange={(e) => setRoleFilter(e.target.value)}
                    >
                        {ROLE_OPTIONS.map((opt) => (
                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                        ))}
                    </select>

                    {/* Status Filter */}
                    <select
                        id="filter-status"
                        className="input"
                        style={{ width: 160 }}
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        {STATUS_OPTIONS.map((opt) => (
                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                        ))}
                    </select>
                </div>

                <button
                    id="btn-add-crew"
                    className="btn btn-primary"
                    onClick={() => setShowAddModal(true)}
                >
                    + Add Crew
                </button>
            </div>

            {/* Results count */}
            <div style={{ fontSize: 13, color: "var(--slate-500)", marginBottom: 12 }}>
                Showing <strong>{filteredCrew.length}</strong> of {DEMO_CREW.length} crew members
            </div>

            {/* Data Table */}
            <div className="glass-card" style={{ overflow: "hidden" }}>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>STAFF ID</th>
                            <th>NAME</th>
                            <th>ROLE</th>
                            <th>BASE</th>
                            <th>STATUS</th>
                            <th style={{ textAlign: "right" }}>ACTIONS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredCrew.map((crew) => (
                            <tr key={crew.id}>
                                <td>
                                    <span style={{ fontWeight: 600, color: "var(--sky-700)", fontFamily: "var(--font-mono, monospace)", fontSize: 13 }}>
                                        {crew.staffId}
                                    </span>
                                </td>
                                <td>
                                    <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                                        <div
                                            style={{
                                                width: 36, height: 36, borderRadius: 10,
                                                background: `linear-gradient(135deg, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-400)" : "#a855f7"}, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-600)" : "#7c3aed"})`,
                                                display: "flex", alignItems: "center", justifyContent: "center",
                                                color: "white", fontSize: 14, fontWeight: 700,
                                            }}
                                        >
                                            {crew.firstName[0]}{crew.lastName[0]}
                                        </div>
                                        <div>
                                            <div style={{ fontWeight: 600, color: "var(--slate-800)", fontSize: 14 }}>
                                                {crew.fullName}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td><RoleBadge role={crew.crewRole} category={crew.crewCategory} /></td>
                                <td>
                                    <span style={{ fontWeight: 600, color: "var(--slate-600)", fontSize: 13 }}>
                                        {crew.baseStation}
                                    </span>
                                </td>
                                <td><StatusBadge status={crew.status} /></td>
                                <td style={{ textAlign: "right" }}>
                                    <Link
                                        href={`/crew/${crew.id}`}
                                        className="btn btn-outline btn-sm"
                                    >
                                        View
                                    </Link>
                                </td>
                            </tr>
                        ))}
                        {filteredCrew.length === 0 && (
                            <tr>
                                <td colSpan={6} style={{ textAlign: "center", padding: 48, color: "var(--slate-400)" }}>
                                    No crew members match your filters.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Add Crew Modal */}
            {showAddModal && <AddCrewModal onClose={() => setShowAddModal(false)} />}
        </div>
    );
}

function AddCrewModal({ onClose }: { onClose: () => void }): React.JSX.Element {
    return (
        <div
            style={{
                position: "fixed", inset: 0, background: "rgba(0,0,0,0.4)",
                backdropFilter: "blur(4px)", display: "flex",
                alignItems: "center", justifyContent: "center", zIndex: 50,
            }}
            onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
        >
            <div
                className="animate-fade-in-up"
                style={{
                    background: "white", borderRadius: 16, padding: 32,
                    width: "100%", maxWidth: 520,
                    boxShadow: "0 24px 48px rgba(0,0,0,0.12)",
                }}
            >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
                    <h2 style={{ fontSize: 20, fontWeight: 700, color: "var(--slate-900)", margin: 0 }}>
                        Register New Crew
                    </h2>
                    <button onClick={onClose} style={{ background: "none", border: "none", fontSize: 20, cursor: "pointer", color: "var(--slate-400)" }}>
                        ✕
                    </button>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
                    <div>
                        <label className="label" htmlFor="inp-staff-id">Staff ID</label>
                        <input id="inp-staff-id" className="input" placeholder="SKY-006" />
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-email">Email</label>
                        <input id="inp-email" className="input" type="email" placeholder="name@airline.com" />
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-first-name">First Name</label>
                        <input id="inp-first-name" className="input" placeholder="John" />
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-last-name">Last Name</label>
                        <input id="inp-last-name" className="input" placeholder="Doe" />
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-role">Role</label>
                        <select id="inp-role" className="input">
                            <option value="">Select role...</option>
                            <option value="CAPTAIN">Captain</option>
                            <option value="FIRST_OFFICER">First Officer</option>
                            <option value="PURSER">Purser</option>
                            <option value="FLIGHT_ATTENDANT">Flight Attendant</option>
                        </select>
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-base">Base Station</label>
                        <input id="inp-base" className="input" placeholder="CGK" maxLength={5} />
                    </div>
                </div>

                <div style={{ display: "flex", gap: 12, justifyContent: "flex-end", marginTop: 28 }}>
                    <button className="btn btn-outline" onClick={onClose}>Cancel</button>
                    <button className="btn btn-primary">Register Crew</button>
                </div>
            </div>
        </div>
    );
}
