export default function DashboardPage(): React.JSX.Element {
  const STAT_CARDS = [
    { label: "Active Crew", value: "—", icon: "👨‍✈️", color: "var(--sky-500)" },
    { label: "Grounded", value: "—", icon: "🚫", color: "var(--red-500)" },
    { label: "Expiring Docs", value: "—", icon: "⚠️", color: "var(--amber-500)" },
    { label: "Flights Today", value: "—", icon: "✈️", color: "var(--green-500)" },
  ];

  return (
    <div className="animate-fade-in-up">
      {/* Stats Grid */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 20, marginBottom: 32 }}>
        {STAT_CARDS.map((stat) => (
          <div key={stat.label} className="glass-card" style={{ padding: 24 }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
              <div>
                <div style={{ fontSize: 13, color: "var(--slate-500)", fontWeight: 500, marginBottom: 8 }}>
                  {stat.label}
                </div>
                <div style={{ fontSize: 32, fontWeight: 700, color: "var(--slate-900)" }}>
                  {stat.value}
                </div>
              </div>
              <div
                style={{
                  width: 48, height: 48, borderRadius: 12,
                  background: `${stat.color}15`,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontSize: 22,
                }}
              >
                {stat.icon}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="glass-card" style={{ padding: 32 }}>
        <h2 style={{ fontSize: 18, fontWeight: 700, color: "var(--slate-800)", marginBottom: 16, margin: 0 }}>
          Quick Actions
        </h2>
        <p style={{ color: "var(--slate-500)", fontSize: 14, marginTop: 8 }}>
          Navigate to <strong>Crew Members</strong> to manage crew profiles, documents, and compliance status.
          Statistics will populate once the database is connected.
        </p>
      </div>
    </div>
  );
}
