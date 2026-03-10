"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { listCrew, registerCrew } from "@/services/crew-service";
import type { CrewMember } from "@/types";

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

    const [crewList, setCrewList] = useState<CrewMember[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchCrew = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await listCrew({
                role: roleFilter || undefined,
                status: statusFilter || undefined,
                search: search || undefined
            });
            setCrewList(data);
        } catch (err: any) {
            setError(err.message || "Failed to load crew members.");
        } finally {
            setLoading(false);
        }
    };

    // Debounce search and filters
    useEffect(() => {
        const timer = setTimeout(() => {
            fetchCrew();
        }, 300);
        return () => clearTimeout(timer);
    }, [roleFilter, statusFilter, search]);

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

            {/* Results count & Error block */}
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12, alignItems: "center" }}>
                <div style={{ fontSize: 13, color: "var(--slate-500)" }}>
                    Showing <strong>{crewList.length}</strong> crew members
                </div>
                {error && <div style={{ color: "var(--red-600)", fontSize: 13 }}>{error}</div>}
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
                        {loading ? (
                            <tr>
                                <td colSpan={6} style={{ textAlign: "center", padding: 48, color: "var(--slate-400)" }}>
                                    Loading crew data...
                                </td>
                            </tr>
                        ) : crewList.length === 0 ? (
                            <tr>
                                <td colSpan={6} style={{ textAlign: "center", padding: 48, color: "var(--slate-400)" }}>
                                    No crew members match your filters.
                                </td>
                            </tr>
                        ) : (
                            crewList.map((crew) => (
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
                                                    {crew.fullName || `${crew.firstName} ${crew.lastName}`}
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
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Add Crew Modal */}
            {showAddModal && (
                <AddCrewModal
                    onClose={() => setShowAddModal(false)}
                    onSuccess={() => {
                        setShowAddModal(false);
                        fetchCrew(); // Refetch
                    }}
                />
            )}
        </div>
    );
}

function AddCrewModal({ onClose, onSuccess }: { onClose: () => void, onSuccess: () => void }): React.JSX.Element {
    const [formData, setFormData] = useState({
        staffId: "",
        email: "",
        firstName: "",
        lastName: "",
        crewRole: "",
        baseStation: ""
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setIsSubmitting(true);
            setError(null);
            await registerCrew(formData);
            onSuccess();
        } catch (err: any) {
            setError(err.message || "Failed to register crew");
        } finally {
            setIsSubmitting(false);
        }
    };

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

                <form onSubmit={handleSubmit}>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16, marginBottom: 24 }}>
                        <div>
                            <label className="label" htmlFor="inp-staff-id">Staff ID</label>
                            <input
                                id="inp-staff-id" required className="input" placeholder="SKY-006"
                                value={formData.staffId} onChange={e => setFormData({ ...formData, staffId: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="label" htmlFor="inp-email">Email</label>
                            <input
                                id="inp-email" required className="input" type="email" placeholder="name@airline.com"
                                value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="label" htmlFor="inp-first-name">First Name</label>
                            <input
                                id="inp-first-name" required className="input" placeholder="John"
                                value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="label" htmlFor="inp-last-name">Last Name</label>
                            <input
                                id="inp-last-name" required className="input" placeholder="Doe"
                                value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })}
                            />
                        </div>
                        <div style={{ gridColumn: "span 2" }}>
                            <label className="label" htmlFor="inp-role">Role</label>
                            <select
                                id="inp-role" required className="input"
                                value={formData.crewRole} onChange={e => setFormData({ ...formData, crewRole: e.target.value })}
                            >
                                <option value="">Select role...</option>
                                <option value="CAPTAIN">Captain</option>
                                <option value="FIRST_OFFICER">First Officer</option>
                                <option value="PURSER">Purser</option>
                                <option value="FLIGHT_ATTENDANT">Flight Attendant</option>
                            </select>
                        </div>
                        <div style={{ gridColumn: "span 2" }}>
                            <label className="label" htmlFor="inp-base">Base Station</label>
                            <input
                                id="inp-base" required className="input" placeholder="CGK" maxLength={5}
                                value={formData.baseStation} onChange={e => setFormData({ ...formData, baseStation: e.target.value.toUpperCase() })}
                            />
                        </div>
                    </div>

                    {error && (
                        <div style={{ padding: "12px", background: "var(--red-50)", color: "var(--red-700)", borderRadius: 6, marginBottom: 16, fontSize: 13 }}>
                            {error}
                        </div>
                    )}

                    <div style={{ display: "flex", gap: 12, justifyContent: "flex-end" }}>
                        <button type="button" className="btn btn-outline" onClick={onClose} disabled={isSubmitting}>Cancel</button>
                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                            {isSubmitting ? "Registering..." : "Register Crew"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
