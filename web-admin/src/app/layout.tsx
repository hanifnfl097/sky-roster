import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Sidebar from "@/components/ui/sidebar";
import Header from "@/components/ui/header";

const inter = Inter({
  variable: "--font-geist-sans",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
});

export const metadata: Metadata = {
  title: "SkyRoster — Crew Management System",
  description:
    "Enterprise-grade aviation crew management dashboard for Flight and Cabin Crew operations, CASR 121 / ICAO compliant.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>): React.JSX.Element {
  return (
    <html lang="en">
      <body className={`${inter.variable} antialiased`}>
        <Sidebar />
        <div style={{ marginLeft: "var(--sidebar-width)" }}>
          <Header />
          <main style={{ padding: "24px 32px", minHeight: "calc(100vh - var(--header-height))" }}>
            {children}
          </main>
        </div>
      </body>
    </html>
  );
}
