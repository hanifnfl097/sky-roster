"use client";

import { useParams } from "next/navigation";
import Link from "next/link";
import { useState } from "react";

/** Demo crew detail data */
const DEMO_CREW_DETAIL: Record<string, {
    id: string; staffId: string; firstName: string; lastName: string;
    crewRole: string; crewCategory: string; baseStation: string;
    status: string; groundingReason: string | null; groundedAt: string | null;
    fullName: string; createdAt: string; updatedAt: string;
    documents: Array<{
        id: string; documentType: string; documentNumber: string;
        issueDate: string; expiryDate: string; status: string; daysUntilExpiry: number;
    }>;
}> = {
    "1": {
        id: "1", staffId: "SKY-001", firstName: "Budi", lastName: "Hartono",
        crewRole: "CAPTAIN", crewCategory: "FLIGHT_CREW", baseStation: "CGK",
        status: "ACTIVE", groundingReason: null, groundedAt: null,
        fullName: "Budi Hartono",
        createdAt: "2026-01-15T08:00:00Z", updatedAt: "2026-03-01T10:00:00Z",
        documents: [
            { id: "d1", documentType: "ATPL", documentNumber: "ATPL-2024-001", issueDate: "2024-06-01", expiryDate: "2027-06-01", status: "VALID", daysUntilExpiry: 448 },
            { id: "d2", documentType: "MEDEX", documentNumber: "MED-2025-099", issueDate: "2025-12-01", expiryDate: "2026-12-01", status: "VALID", daysUntilExpiry: 266 },
            { id: "d3", documentType: "SIM_CHECK", documentNumber: "SIM-2026-003", issueDate: "2026-01-20", expiryDate: "2026-07-20", status: "EXPIRING_SOON", daysUntilExpiry: 132 },
            { id: "d4", documentType: "TYPE_RATING_CERT", documentNumber: "TRC-B737-012", issueDate: "2025-03-15", expiryDate: "2026-03-15", status: "EXPIRING_SOON", daysUntilExpiry: 5 },
            { id: "d5", documentType: "SEP", documentNumber: "SEP-2025-088", issueDate: "2025-06-01", expiryDate: "2026-06-01", status: "VALID", daysUntilExpiry: 83 },
            { id: "d6", documentType: "INSTRUMENT_RATING", documentNumber: "IR-2025-022", issueDate: "2025-09-01", expiryDate: "2026-09-01", status: "VALID", daysUntilExpiry: 175 },
        ],
    },
    "3": {
        id: "3", staffId: "SKY-003", firstName: "Wayan", lastName: "Putra",
        crewRole: "PURSER", crewCategory: "CABIN_CREW", baseStation: "DPS",
        status: "GROUNDED", groundingReason: "MEDEX expired on 2026-03-01",
        groundedAt: "2026-03-01T06:00:00Z", fullName: "Wayan Putra",
        createdAt: "2026-02-01T08:00:00Z", updatedAt: "2026-03-05T10:00:00Z",
        documents: [
            { id: "d7", documentType: "MEDEX", documentNumber: "MED-2025-045", issueDate: "2025-03-01", expiryDate: "2026-03-01", status: "EXPIRED", daysUntilExpiry: -9 },
            { id: "d8", documentType: "SEP", documentNumber: "SEP-2025-032", issueDate: "2025-08-01", expiryDate: "2026-08-01", status: "VALID", daysUntilExpiry: 144 },
            { id: "d9", documentType: "TYPE_RATING_CERT", documentNumber: "TRC-A320-005", issueDate: "2025-05-01", expiryDate: "2026-05-01", status: "VALID", daysUntilExpiry: 52 },
        ],
    },
};

const WARNING_THRESHOLD_DAYS = 30;
const CRITICAL_THRESHOLD_DAYS = 7;

function DocStatusBadge({ status, daysUntilExpiry }: { status: string; daysUntilExpiry: number }): React.JSX.Element {
    const classMap: Record<string, string> = {
        VALID: "badge-valid",
        EXPIRING_SOON: "badge-expiring-soon",
        EXPIRED: "badge-expired",
    };
    return (
        <span className={`badge ${classMap[status] || ""}`}>
            {status === "EXPIRED" ? `EXPIRED (${Math.abs(daysUntilExpiry)}d ago)` :
                status === "EXPIRING_SOON" ? `${daysUntilExpiry}d left` :
                    `${daysUntilExpiry}d left`}
        </span>
    );
}

export default function CrewDetailPage(): React.JSX.Element {
    const params = useParams();
    const crewId = params.id as string;
    const crew = DEMO_CREW_DETAIL[crewId];
    const [showDocModal, setShowDocModal] = useState(false);

    if (!crew) {
        return (
            <div className="animate-fade-in-up" style={{ textAlign: "center", padding: 64 }}>
                <div style={{ fontSize: 48, marginBottom: 16 }}>🔍</div>
                <h2 style={{ color: "var(--slate-700)", marginBottom: 8 }}>Crew Not Found</h2>
                <p style={{ color: "var(--slate-500)" }}>
                    Demo data available for crew IDs: 1 and 3.
                </p>
                <Link href="/crew" className="btn btn-primary" style={{ marginTop: 16, display: "inline-flex" }}>
                    ← Back to Crew List
                </Link>
            </div>
        );
    }

    const statusColor = crew.status === "ACTIVE" ? "var(--green-500)" : crew.status === "GROUNDED" ? "var(--red-500)" : "var(--amber-500)";
    const statusBg = crew.status === "ACTIVE" ? "var(--green-100)" : crew.status === "GROUNDED" ? "var(--red-100)" : "var(--amber-100)";
    const criticalDocs = crew.documents.filter(
        (d) => d.status === "EXPIRED" || d.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS
    );

    return (
        <div className="animate-fade-in-up">
            {/* Back nav */}
            <Link href="/crew" style={{ display: "inline-flex", alignItems: "center", gap: 6, color: "var(--slate-500)", fontSize: 14, textDecoration: "none", marginBottom: 20 }}>
                ← Back to Crew List
            </Link>

            {/* Profile Card */}
            <div className="glass-card" style={{ padding: 32, marginBottom: 24 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                    {/* Left: Avatar + Info */}
                    <div style={{ display: "flex", gap: 24, alignItems: "center" }}>
                        <div
                            style={{
                                width: 80, height: 80, borderRadius: 20,
                                background: `linear-gradient(135deg, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-400)" : "#a855f7"}, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-600)" : "#7c3aed"})`,
                                display: "flex", alignItems: "center", justifyContent: "center",
                                color: "white", fontSize: 28, fontWeight: 700,
                                boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
                            }}
                        >
                            {crew.firstName[0]}{crew.lastName[0]}
                        </div>
                        <div>
                            <h2 style={{ fontSize: 24, fontWeight: 700, color: "var(--slate-900)", margin: 0 }}>
                                {crew.fullName}
                            </h2>
                            <div style={{ display: "flex", gap: 16, marginTop: 8 }}>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>ID:</strong> {crew.staffId}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Role:</strong> {crew.crewRole.replace("_", " ")}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Base:</strong> {crew.baseStation}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Category:</strong> {crew.crewCategory.replace("_", " ")}
                                </span>
                            </div>
                        </div>
                    </div>

                    {/* Right: Status + Actions */}
                    <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: 12 }}>
                        <span
                            style={{
                                padding: "8px 20px", borderRadius: 12, fontSize: 14, fontWeight: 700,
                                background: statusBg, color: statusColor,
                                border: `1px solid ${statusColor}30`,
                            }}
                        >
                            ● {crew.status.replace("_", " ")}
                        </span>
                        <div style={{ display: "flex", gap: 8 }}>
                            {crew.status === "ACTIVE" && (
                                <button className="btn btn-danger btn-sm">🚫 Ground</button>
                            )}
                            {crew.status === "GROUNDED" && (
                                <button className="btn btn-primary btn-sm">✅ Activate</button>
                            )}
                            <button className="btn btn-outline btn-sm">✏️ Edit</button>
                        </div>
                    </div>
                </div>

                {/* Grounding Alert */}
                {crew.status === "GROUNDED" && crew.groundingReason && (
                    <div
                        style={{
                            marginTop: 20, padding: "14px 20px", borderRadius: 10,
                            background: "var(--red-100)", border: "1px solid rgba(239,68,68,0.2)",
                            display: "flex", alignItems: "center", gap: 12,
                        }}
                    >
                        <span style={{ fontSize: 20 }}>⚠️</span>
                        <div>
                            <div style={{ fontWeight: 600, color: "var(--red-700)", fontSize: 14 }}>
                                Grounded Since {crew.groundedAt ? new Date(crew.groundedAt).toLocaleDateString() : "Unknown"}
                            </div>
                            <div style={{ color: "var(--red-700)", fontSize: 13, opacity: 0.85 }}>
                                Reason: {crew.groundingReason}
                            </div>
                        </div>
                    </div>
                )}
            </div>

            {/* Critical Alert */}
            {criticalDocs.length > 0 && (
                <div
                    className="animate-slide-in"
                    style={{
                        padding: "16px 20px", borderRadius: 12, marginBottom: 24,
                        background: "linear-gradient(135deg, rgba(239,68,68,0.06), rgba(245,158,11,0.06))",
                        border: "1px solid rgba(239,68,68,0.15)",
                        display: "flex", alignItems: "center", gap: 12,
                    }}
                >
                    <span style={{ fontSize: 24 }}>🚨</span>
                    <div>
                        <div style={{ fontWeight: 700, color: "var(--red-700)", fontSize: 14 }}>
                            {criticalDocs.length} Document{criticalDocs.length > 1 ? "s" : ""} Require Attention
                        </div>
                        <div style={{ fontSize: 13, color: "var(--slate-600)" }}>
                            {criticalDocs.map(d => d.documentType.replace("_", " ")).join(", ")} — renewal needed immediately.
                        </div>
                    </div>
                </div>
            )}

            {/* Documents Section */}
            <div className="glass-card" style={{ overflow: "hidden" }}>
                <div style={{ padding: "20px 24px", display: "flex", justifyContent: "space-between", alignItems: "center", borderBottom: "1px solid var(--border)" }}>
                    <h3 style={{ fontSize: 16, fontWeight: 700, color: "var(--slate-800)", margin: 0 }}>
                        Documents & Certificates ({crew.documents.length})
                    </h3>
                    <button className="btn btn-primary btn-sm" onClick={() => setShowDocModal(true)}>
                        + Add Document
                    </button>
                </div>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>TYPE</th>
                            <th>NUMBER</th>
                            <th>ISSUE DATE</th>
                            <th>EXPIRY DATE</th>
                            <th>STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {crew.documents.map((doc) => (
                            <tr key={doc.id}>
                                <td style={{ fontWeight: 600, color: "var(--slate-800)" }}>
                                    {doc.documentType.replace(/_/g, " ")}
                                </td>
                                <td style={{ fontFamily: "var(--font-mono, monospace)", fontSize: 13, color: "var(--slate-600)" }}>
                                    {doc.documentNumber}
                                </td>
                                <td>{doc.issueDate}</td>
                                <td>
                                    <span style={{
                                        fontWeight: doc.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS ? 700 : 400,
                                        color: doc.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS ? "var(--red-700)" : "var(--slate-700)",
                                    }}>
                                        {doc.expiryDate}
                                    </span>
                                </td>
                                <td><DocStatusBadge status={doc.status} daysUntilExpiry={doc.daysUntilExpiry} /></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Add Document Modal */}
            {showDocModal && <AddDocumentModal onClose={() => setShowDocModal(false)} />}
        </div>
    );
}

function AddDocumentModal({ onClose }: { onClose: () => void }): React.JSX.Element {
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
                    width: "100%", maxWidth: 480,
                    boxShadow: "0 24px 48px rgba(0,0,0,0.12)",
                }}
            >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
                    <h2 style={{ fontSize: 20, fontWeight: 700, color: "var(--slate-900)", margin: 0 }}>
                        Add Document
                    </h2>
                    <button onClick={onClose} style={{ background: "none", border: "none", fontSize: 20, cursor: "pointer", color: "var(--slate-400)" }}>✕</button>
                </div>

                <div style={{ display: "grid", gap: 16 }}>
                    <div>
                        <label className="label" htmlFor="inp-doc-type">Document Type</label>
                        <select id="inp-doc-type" className="input">
                            <option value="">Select type...</option>
                            <option value="MEDEX">MEDEX</option>
                            <option value="ATPL">ATPL</option>
                            <option value="CPL">CPL</option>
                            <option value="INSTRUMENT_RATING">Instrument Rating</option>
                            <option value="SEP">SEP</option>
                            <option value="SIM_CHECK">Sim Check</option>
                            <option value="TYPE_RATING_CERT">Type Rating Certificate</option>
                        </select>
                    </div>
                    <div>
                        <label className="label" htmlFor="inp-doc-number">Document Number</label>
                        <input id="inp-doc-number" className="input" placeholder="ATPL-2026-001" />
                    </div>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                        <div>
                            <label className="label" htmlFor="inp-issue-date">Issue Date</label>
                            <input id="inp-issue-date" className="input" type="date" />
                        </div>
                        <div>
                            <label className="label" htmlFor="inp-expiry-date">Expiry Date</label>
                            <input id="inp-expiry-date" className="input" type="date" />
                        </div>
                    </div>
                </div>

                <div style={{ display: "flex", gap: 12, justifyContent: "flex-end", marginTop: 28 }}>
                    <button className="btn btn-outline" onClick={onClose}>Cancel</button>
                    <button className="btn btn-primary">Save Document</button>
                </div>
            </div>
        </div>
    );
}
